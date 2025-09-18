package org.openmrs.module.sespct.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.ExportService;
import org.openmrs.module.sespct.api.PedidoService;
import org.openmrs.module.sespct.api.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/module/sespct/")
public class SespctController {
	
	@Autowired
	private PedidoService pedidoService;
	
	@Autowired
	private ExportService exportService;
	
	private static final Logger log = LoggerFactory.getLogger(SespctController.class);
	
	@RequestMapping(value = "sespct.form", method = RequestMethod.GET)
	public String showMainPage(ModelMap model, @RequestParam(value = "startDate", required = false) String startDateStr,
	        @RequestParam(value = "endDate", required = false) String endDateStr,
	        @RequestParam(value = "estado", required = false) String estado,
	        @RequestParam(value = "ncft", required = false) String ncft,
	        @RequestParam(value = "nid", required = false) String nid,
	        @RequestParam(value = "usCode", required = false) String usCode,
	        @RequestParam(value = "flashMessage", required = false) String flashMessage) {
		
		DateTimeFormatter ptFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		DateTimeFormatter enFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
		
		LocalDate startDate = null;
		LocalDate endDate = null;
		
		try {
			// Call the main sync method every time the page is loaded
			pedidoService.synchronizeMiddlewareData();
		}
		catch (Exception e) {
			log.error("An error occurred during middleware synchronization.", e);
			model.addAttribute("errorMessage", "Ocorreu um erro durante a sincronização: " + e.getMessage());
		}
		
		try {
			if (flashMessage != null && !flashMessage.trim().isEmpty()) {
				model.addAttribute("flashMessage", flashMessage);
			}
			
			if (startDateStr != null && !startDateStr.trim().isEmpty()) {
				try {
					startDate = LocalDate.parse(startDateStr, ptFormatter);
				}
				catch (DateTimeParseException e1) {
					try {
						startDate = LocalDate.parse(startDateStr, enFormatter);
					}
					catch (DateTimeParseException e2) {
						// Fallback to the ISO standard format
						startDate = LocalDate.parse(startDateStr, isoFormatter);
					}
				}
			}
			
			if (endDateStr != null && !endDateStr.trim().isEmpty()) {
				try {
					endDate = LocalDate.parse(endDateStr, ptFormatter);
				}
				catch (DateTimeParseException e1) {
					try {
						endDate = LocalDate.parse(endDateStr, enFormatter);
					}
					catch (DateTimeParseException e2) {
						// Fallback to the ISO standard format
						endDate = LocalDate.parse(endDateStr, isoFormatter);
					}
				}
			}
			
			// Remove spaces from free text fields as per requirements
			String trimmedNcft = (ncft != null) ? ncft.trim() : null;
			String trimmedNid = (nid != null) ? nid.trim() : null;
			
			List<Pedido> requests = pedidoService.searchPedidos(startDate, endDate, estado, trimmedNcft, trimmedNid, usCode);
			
			// Add all results and search parameters back to the model
			// This is important to re-populate the form fields after submission
			model.addAttribute("pedidos", requests);
			model.addAttribute("startDate", startDateStr);
			model.addAttribute("endDate", endDateStr);
			model.addAttribute("selectedEstado", estado);
			model.addAttribute("ncft", trimmedNcft);
			model.addAttribute("nid", trimmedNid);
			model.addAttribute("selectedUsCode", usCode);
			
			// You'll also need to load the Unidades Sanitarias for the dropdown
			String unidadesSanitaria = Context.getAdministrationService().getGlobalProperty("sespct.api.usCode");
			model.addAttribute("unidadesSanitaria", unidadesSanitaria);
			
			log.info("Found " + requests.size() + " SESP-CT requests based on search criteria");
			
		}
		catch (DateTimeParseException e) {
			// This will now catch a failure only if NONE of the three formats match
			log.error("Invalid date format submitted", e);
			model.addAttribute("errorMessage", "Formato de data inválido. Use dd-MM-yyyy, MM/dd/yyyy, ou yyyy-MM-dd.");
		}
		catch (DataAccessException e) {
			log.error("Error searching SESP-CT requests", e);
			model.addAttribute("errorMessage",
			    Context.getMessageSourceService().getMessage("sespct.error.loadingData") + e.getMessage());
		}
		
		return "/module/sespct/sespct/index";
	}
	
	@RequestMapping(value = "/module/sespct/viewRequest", method = RequestMethod.GET)
	public String viewRequest(@RequestParam("pedidoId") String pedidoId, ModelMap model) {
		
		try {
			Pedido request = pedidoService.getPedidoByExternalId(pedidoId);
			
			if (request != null) {
				model.addAttribute("pedido", request);
				return "/module/sespct/viewRequest";
			} else {
				model.addAttribute("errorMessage", "Request not found: " + pedidoId);
				return "redirect:/module/sespct/sespct.form";
			}
			
		}
		catch (Exception e) {
			log.error("Error loading SESP-CT request: " + pedidoId, e);
			model.addAttribute("errorMessage", "Error loading request: " + e.getMessage());
			return "redirect:/module/sespct/sespct.form";
		}
	}
	
	/**
	 * Export functionality
	 */
	@RequestMapping(value = "manageftcases/export.form", method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadExcel(@RequestParam(value = "startDate", required = false) String startDateString,
	        @RequestParam(value = "endDate", required = false) String endDateString,
	        @RequestParam(value = "estado", required = false) String estado,
	        @RequestParam(value = "ncft", required = false) String ncft,
	        @RequestParam(value = "nid", required = false) String nid,
	        @RequestParam(value = "usCode", required = false) String usCode) {
		try {
			log.info("Starting Excel export with provided filters.");
			
			// --- Date Parsing (this logic is correct) ---
			LocalDate startDate = null;
			if (startDateString != null && !startDateString.trim().isEmpty()) {
				startDate = parseLocalDateMultiFormat(startDateString);
			}
			
			LocalDate endDate = null;
			if (endDateString != null && !endDateString.trim().isEmpty()) {
				endDate = parseLocalDateMultiFormat(endDateString);
			}
			
			// --- Clean up other parameters ---
			String trimmedNcft = (ncft != null) ? ncft.trim() : null;
			String trimmedNid = (nid != null) ? nid.trim() : null;
			
			// --- Call the comprehensive search service ---
			List<Pedido> pedidos = pedidoService.searchPedidos(startDate, endDate, estado, trimmedNcft, trimmedNid, usCode);
			System.out.println(pedidos);
			log.info("Retrieved {} pedidos based on search criteria for export.", pedidos.size());
			
			//			// --- Package filters for the report generator's subtitle ---
			//			Map<String, String> searchFilters = new LinkedHashMap<>();
			//			if (startDateString != null && !startDateString.isEmpty()) searchFilters.put("Data Início", startDateString);
			//			if (endDateString != null && !endDateString.isEmpty()) searchFilters.put("Data Fim", endDateString);
			//			if (estado != null && !estado.isEmpty() && !"ALL".equalsIgnoreCase(estado)) searchFilters.put("Estado", estado);
			//			if (trimmedNcft != null && !trimmedNcft.isEmpty()) searchFilters.put("NCFT", trimmedNcft);
			//			if (trimmedNid != null && !trimmedNid.isEmpty()) searchFilters.put("NID", trimmedNid);
			//			if (usCode != null && !usCode.isEmpty() && !"ALL".equalsIgnoreCase(usCode)) searchFilters.put("US", usCode);
			
			// --- Generate the report ---
			byte[] excelBytes = exportService.generatePedidoReport(pedidos, startDateString, endDateString);
			log.info("Generated Excel file with {} bytes", excelBytes.length);
			
			return createSuccessResponse(excelBytes, startDate, endDate);
			
		}
		catch (DateTimeParseException e) {
			log.error("Error parsing date for XLSX export: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
			    ("Invalid date format provided. " + e.getMessage()).getBytes());
		}
		catch (Exception e) {
			log.error("Error generating XLSX export", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
			    ("Error generating export: " + e.getMessage()).getBytes());
		}
	}
	
	/**
	 * Parses a date string using a list of known formats.
	 * 
	 * @param dateString The string representation of the date.
	 * @return A {@link LocalDate} object.
	 * @throws DateTimeParseException if the string cannot be parsed by any known format.
	 */
	private LocalDate parseLocalDateMultiFormat(String dateString) {
		// 4. Use the immutable and thread-safe DateTimeFormatter
		List<DateTimeFormatter> knownFormatters = Arrays.asList(DateTimeFormatter.ofPattern("MM/dd/yyyy"), // English format
		    DateTimeFormatter.ofPattern("dd-MM-yyyy") // Portuguese format
		        );
		
		for (DateTimeFormatter formatter : knownFormatters) {
			try {
				return LocalDate.parse(dateString, formatter);
			}
			catch (DateTimeParseException e) {
				// Ignore and try the next format
			}
		}
		// If no format matches, throw a specific exception
		throw new DateTimeParseException("Invalid date format for value: \"" + dateString + "\"", dateString, 0);
	}
	
	/**
	 * Helper to build the final file download response.
	 */
	private ResponseEntity<byte[]> createSuccessResponse(byte[] excelBytes, LocalDate startDate, LocalDate endDate) {
		// This formatter is for the filename, so it should be a standard, clean format.
		DateTimeFormatter filenameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String filename;
		
		// Logic to build the filename based on which dates are available
		if (startDate != null && endDate != null) {
			// Case 1: Both dates are provided
			String start = startDate.format(filenameFormatter);
			String end = endDate.format(filenameFormatter);
			filename = String.format("SESP_SCT_Export_%s_to_%s.xlsx", start, end);
		} else if (startDate != null) {
			// Case 2: Only start date is provided
			String start = startDate.format(filenameFormatter);
			filename = String.format("SESP_SCT_Export_from_%s.xlsx", start);
		} else if (endDate != null) {
			// Case 3: Only end date is provided
			String end = endDate.format(filenameFormatter);
			filename = String.format("SESP_SCT_Export_until_%s.xlsx", end);
		} else {
			// Case 4: No dates are provided (fallback)
			String today = LocalDate.now().format(filenameFormatter);
			filename = String.format("SESP_SCT_Export_AllData_%s.xlsx", today);
		}
		
		return ResponseEntity.ok()
		        .contentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
		        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"").body(excelBytes);
	}
}
