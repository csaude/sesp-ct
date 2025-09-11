package org.openmrs.module.sespct.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.ExportService;
import org.openmrs.module.sespct.api.PedidoService;
import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.util.DateRange;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
	public String showMainPage(@Valid Pedido pedido, ModelMap model,
	        @RequestParam(value = "estado", required = false) String estado,
	        @RequestParam(value = "flashMessage", required = false) String flashMessage, HttpServletRequest request) {
		
		try {
			// Handle flash message from URL parameter (similar to your existing DISA module)
			if (flashMessage != null && !flashMessage.trim().isEmpty()) {
				model.addAttribute("flashMessage", flashMessage);
				log.info("Flash message received: {}", flashMessage);
			}
			
			List<Pedido> requests;
			if (estado != null && !estado.trim().isEmpty()) {
				requests = pedidoService.getPedidosByEstado(estado);
			} else {
				requests = pedidoService.getAllPedidos();
			}
			
			model.addAttribute("pedidos", requests);
			model.addAttribute("selectedEstado", estado);
			
			log.info("Loaded " + requests.size() + " SESP-CT requests for display");
			
		}
		catch (DataAccessException e) {
			log.error("Error loading SESP-CT requests", e);
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
	public ResponseEntity<byte[]> downloadExcel(@RequestParam("startDate") String startDateString,
	        @RequestParam("endDate") String endDateString) {
		try {
			log.info("Starting export with date strings: {} to {}", startDateString, endDateString);
			
			DateRange dateRange = parseAndValidateDateRange(startDateString, endDateString);
			log.info("Parsed dates successfully. Range: {}", dateRange);
			
			List<Pedido> pedidos = pedidoService.getPedidosByDateTimeRange(dateRange.startDateTime(),
			    dateRange.endDateTime());
			log.info("Retrieved {} pedidos", pedidos.size());
			
			byte[] excelBytes = exportService.generatePedidoReport(pedidos, startDateString, endDateString);
			log.info("Generated Excel file with {} bytes", excelBytes.length);
			
			return createSuccessResponse(excelBytes, dateRange);
			
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
	 * This is the new helper method requested by the code review. It encapsulates all date parsing
	 * and object creation logic.
	 */
	private DateRange parseAndValidateDateRange(String startDateString, String endDateString) {
		LocalDate startDate = parseLocalDateMultiFormat(startDateString);
		LocalDate endDate = parseLocalDateMultiFormat(endDateString);
		
		// Create and return the new DateRange object
		return new DateRange(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
	}
	
	/**
	 * Helper to build the final file download response.
	 */
	private ResponseEntity<byte[]> createSuccessResponse(byte[] excelBytes, DateRange dateRange) {
		// Define the desired format. This object is thread-safe and reusable.
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		
		// Format the dates from the DateRange object.
		String startDateFormatted = dateRange.startDateTime().format(formatter);
		String endDateFormatted = dateRange.endDateTime().format(formatter);
		
		String filename = String.format("SESP_SCT_Export_%s_to_%s.xlsx", startDateFormatted, endDateFormatted);
		
		return ResponseEntity.ok()
		        .contentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
		        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"").body(excelBytes);
	}
}
