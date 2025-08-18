package org.openmrs.module.sespct.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.PedidoService;
import org.openmrs.module.sespct.api.model.DadosUtente;
import org.openmrs.module.sespct.api.model.Pedido;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.openmrs.module.sespct.api.model.Resposta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/module/sespct/")
public class SespctController {
	
	private static final Logger log = LoggerFactory.getLogger(SespctController.class);
	
	@RequestMapping(value = "sespct.form", method = RequestMethod.GET)
	public String showMainPage(@Valid Pedido pedido, ModelMap model,
	        @RequestParam(value = "estado", required = false) String estado) {
		
		try {
			PedidoService pedidoService = Context.getService(PedidoService.class);
			
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
		catch (Exception e) {
			log.error("Error loading SESP-CT requests", e);
			model.addAttribute("errorMessage", "Error loading data: " + e.getMessage());
		}
		
		return "/module/sespct/sespct/index";
	}
	
	/**
	 * Export functionality
	 */
	@RequestMapping(value = "manageftcases/export.form", method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadExcel(@RequestParam("startDate") String startDate,
	        @RequestParam("endDate") String endDate) throws IOException {
		
		try {
			log.info("Starting export with dates: {} to {}", startDate, endDate);
			
			// Parse dates - expecting MM/dd/yyyy format from frontend
			SimpleDateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date start = inputDateFormat.parse(startDate);
			Date end = inputDateFormat.parse(endDate);
			
			log.info("Parsed dates successfully: {} to {}", start, end);
			
			// Adjust end date to include the entire day
			Calendar cal = Calendar.getInstance();
			cal.setTime(end);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			end = cal.getTime();
			
			// Get filtered data
			PedidoService pedidoService = Context.getService(PedidoService.class);
			List<Pedido> pedidos = pedidoService.getPedidosByDateRange(start, end);
			
			log.info("Retrieved {} pedidos", pedidos.size());
			
			// Generate the Excel file
			byte[] excelBytes = generateExcelReport(pedidos, startDate, endDate);
			
			log.info("Generated Excel file with {} bytes", excelBytes.length);
			
			String filename = "SESP_SCT_Export_" + startDate.replace("/", "_") + "_to_" + endDate.replace("/", "_")
			        + ".xlsx";
			
			return ResponseEntity.ok()
			        .contentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
			        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"").body(excelBytes);
			
		}
		catch (Exception e) {
			log.error("Error generating XLSX export", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
			    ("Error generating export: " + e.getMessage()).getBytes());
		}
	}
	
	private byte[] generateExcelReport(List<Pedido> pedidos, String startDate, String endDate) throws IOException {
        // MODIFICATION: Encapsulated workbook creation and writing in a try-with-resources block
        // This ensures the workbook is always closed, preventing resource leaks.
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("SESP-SCT Export");

            // Create styles
            CellStyle titleStyle = createTitleStyle(workbook);
            // MODIFICATION: Added dedicated styles for the subtitle for a cleaner look.
            CellStyle subtitleLabelStyle = createSubtitleLabelStyle(workbook);
            CellStyle subtitleValueStyle = createSubtitleValueStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateCellStyle = createDateCellStyle(workbook); // MODIFICATION: Renamed for clarity.
            CellStyle centeredDataStyle = createCenteredDataStyle(workbook); // MODIFICATION: Added for centered text data.

            int rowNum = 0;

            // Row 0: Title
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.setHeightInPoints(30); // MODIFICATION: Increased row height for better title visibility.
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Interoperabilidade SESP-SCT");
            titleCell.setCellStyle(titleStyle);
            // MODIFICATION: The column range is 0 to 14, which is 15 columns. This remains correct.
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));

            // Row 1: Period subtitle
            Row periodRow = sheet.createRow(rowNum++);
            periodRow.setHeightInPoints(15); // MODIFICATION: Set row height for subtitle.

            // MODIFICATION: Merged cells for the subtitle label and applied a specific style.
            Cell periodLabelCell = periodRow.createCell(0);
            periodLabelCell.setCellValue("Período de Submissão:");
            periodLabelCell.setCellStyle(subtitleLabelStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1)); // Merge A2:B2

            // MODIFICATION: Merged cells for the subtitle value for better alignment.
            Cell periodValueCell = periodRow.createCell(2);
            periodValueCell.setCellValue(startDate + " - " + endDate);
            periodValueCell.setCellStyle(subtitleValueStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 4)); // Merge C2:E2


            // Row 3: Headers (previously row 2)
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.setHeightInPoints(20); // MODIFICATION: Increased header row height for wrapped text.
            String[] headers = {"US", "NID", "NCFT", "Iniciais", "Sexo", "Idade", "Submissão", "Data de resposta",
                    "Sincronização", "Estado", "Causa de Não processamento", "Linha Terap. (resposta)", "Solicitante email",
                    "Solicitante Tel.", "Email do aprovador"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            for (Pedido pedido : pedidos) {
                Row dataRow = sheet.createRow(rowNum++);
                // MODIFICATION: Passed the new centeredDataStyle to the data population method.
                populateDataRow(dataRow, pedido, dataStyle, dateCellStyle, centeredDataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // MODIFICATION: Increased minimum column width for better readability.
                int minWidth = 12 * 256; // 12 characters width
                if (sheet.getColumnWidth(i) < minWidth) {
                    sheet.setColumnWidth(i, minWidth);
                }
            }

            // Convert to byte array
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
	
	private void populateDataRow(Row row, Pedido pedido, CellStyle dataStyle, CellStyle dateCellStyle,
	        CellStyle centeredDataStyle) {
		int colNum = 0;
		
		// US - Health Unit
		String us = "";
		if (pedido.getDadosUtente() != null) {
			us = pedido.getDadosUtente().getUnidadeSanitaria();
			if (pedido.getDadosUtente().getCodigoUnidadeSanitaria() != null) {
				us += " (" + pedido.getDadosUtente().getCodigoUnidadeSanitaria() + ")";
			}
		}
		createCell(row, colNum++, us, dataStyle);
		
		// NID
		String nid = pedido.getDadosUtente() != null ? pedido.getDadosUtente().getNid() : "";
		createCell(row, colNum++, nid, centeredDataStyle); // MODIFICATION: Centered for consistency.
		
		// NCFT - Request ID
		createCell(row, colNum++, pedido.getPedidoId(), dataStyle);
		
		// Iniciais
		String iniciais = pedido.getDadosUtente() != null ? pedido.getDadosUtente().getIniciais() : "";
		createCell(row, colNum++, iniciais, centeredDataStyle); // MODIFICATION: Centered.
		
		// Sexo
		String sexo = "";
		if (pedido.getDadosUtente() != null && pedido.getDadosUtente().getSexo() != null) {
			String sexoValue = pedido.getDadosUtente().getSexo().toLowerCase();
			sexo = "masculino".equals(sexoValue) ? "M" : ("feminino".equals(sexoValue) ? "F" : pedido.getDadosUtente()
			        .getSexo());
		}
		createCell(row, colNum++, sexo, centeredDataStyle); // MODIFICATION: Centered.
		
		// Idade
		if (pedido.getDadosUtente() != null && pedido.getDadosUtente().getIdade() != null) {
			createNumericCell(row, colNum++, pedido.getDadosUtente().getIdade(), centeredDataStyle);
		} else {
			createCell(row, colNum++, "", centeredDataStyle);
		}
		
		// Submissão
		createDateCell(row, colNum++, pedido.getDataSubmissao(), dateCellStyle);
		
		// Data de resposta
		Date dataResposta = null;
		if (!"Sem resposta".equals(pedido.getEstado()) && !"No Response".equals(pedido.getEstado())) {
			// Get the list of responses
			List<Resposta> respostas = pedido.getRespostas();
			
			// Check if the list is not null and not empty to avoid errors
			if (respostas != null && !respostas.isEmpty()) {
				// Get the last response from the list
				Resposta ultimaResposta = respostas.get(respostas.size() - 1);
				
				// Check that the related objects are not null before getting the date
				if (ultimaResposta != null && ultimaResposta.getRespostaComite() != null) {
					dataResposta = ultimaResposta.getRespostaComite().getDataResposta();
				}
			}
		}
		if (dataResposta != null) {
			createDateCell(row, colNum++, dataResposta, dateCellStyle);
		} else {
			createCell(row, colNum++, "-", centeredDataStyle);
		}
		
		// Sincronização
		Date dataSincronizacao = null;
		List<Resposta> respostas = pedido.getRespostas();
		
		if (respostas != null && !respostas.isEmpty()) {
			Resposta ultimaResposta = respostas.get(respostas.size() - 1);
			if (ultimaResposta != null && ultimaResposta.getMetadados() != null) {
				dataSincronizacao = ultimaResposta.getMetadados().getUltimaSincronizacao();
			}
		}
		createDateCell(row, colNum++, dataSincronizacao, dateCellStyle);
		
		// Estado
		String estado = pedido.getEstado() != null ? pedido.getEstado() : "";
		if ("Sem resposta".equals(estado) || "No Response".equals(estado)) {
			estado = "Sem resposta";
		} else if ("Não Processado".equals(estado) || "Not Processed".equals(estado)) {
			estado = "Não Processado";
		}
		createCell(row, colNum++, estado, dataStyle);
		
		// Causa de Não processamento
		String causa = "-";
		if ("Não Processado".equals(pedido.getEstado()) || "Not Processed".equals(pedido.getEstado())) {
			causa = " NID não encontrado";
		}
		createCell(row, colNum++, causa, dataStyle);
		
		Resposta ultimaResposta = null;
		if (respostas != null && !respostas.isEmpty()) {
			ultimaResposta = respostas.get(respostas.size() - 1);
		}
		
		// Linha Terap. (resposta)
		String linhaTerapeutica = "";
		if (ultimaResposta != null && ultimaResposta.getRespostaComite() != null) {
			linhaTerapeutica = ultimaResposta.getRespostaComite().getLinhaTerapeutica();
		}
		createCell(row, colNum++, linhaTerapeutica, dataStyle);
		
		// Solicitante email
		String solicitanteEmail = pedido.getDadosClinico() != null ? pedido.getDadosClinico().getEmail() : "";
		createCell(row, colNum++, solicitanteEmail, dataStyle);
		
		// Solicitante Tel.
		String solicitanteTelefone = "";
		if (pedido.getDadosClinico() != null) {
			solicitanteTelefone = pedido.getDadosClinico().getTelefone();
		}
		createCell(row, colNum++, solicitanteTelefone, dataStyle);
		
		// Email do aprovador
		String aprovadorEmail = "";
		if (ultimaResposta != null && ultimaResposta.getRespostaComite() != null) {
			aprovadorEmail = ultimaResposta.getRespostaComite().getEmail();
		}
		createCell(row, colNum++, aprovadorEmail, dataStyle);
	}
	
	private void createCell(Row row, int colNum, String value, CellStyle style) {
		Cell cell = row.createCell(colNum);
		cell.setCellValue(value != null ? value : "");
		cell.setCellStyle(style);
	}
	
	private void createDateCell(Row row, int colNum, Date value, CellStyle style) {
		Cell cell = row.createCell(colNum);
		if (value != null) {
			cell.setCellValue(value);
		}
		cell.setCellStyle(style);
	}
	
	private void createNumericCell(Row row, int colNum, Number value, CellStyle style) {
		Cell cell = row.createCell(colNum);
		if (value != null) {
			cell.setCellValue(value.doubleValue());
		}
		cell.setCellStyle(style);
	}
	
	// --- Style Creation Methods ---
	
	private CellStyle createTitleStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short) 15);
		font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		
		return style;
	}
	
	// MODIFICATION: New method for the subtitle label's style.
	private CellStyle createSubtitleLabelStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short) 11);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		return style;
	}
	
	// MODIFICATION: New method for the subtitle value's style.
	private CellStyle createSubtitleValueStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(false); // Subtitle value is not bold
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short) 11);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		return style;
	}
	
	private CellStyle createHeaderStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short) 11); // MODIFICATION: Slightly larger header font.
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		
		// MODIFICATION: Added text wrapping to prevent text from being cut off in headers.
		style.setWrapText(true);
		
		return style;
	}
	
	private CellStyle createDataStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short) 10); // MODIFICATION: Standard data font size.
		style.setFont(font);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setAlignment(HorizontalAlignment.LEFT); // MODIFICATION: Default to left-align for text.
		
		// MODIFICATION: Using a lighter color for borders to be less visually jarring.
		style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		
		return style;
	}
	
	// MODIFICATION: Renamed from createDateStyle to createDateCellStyle for clarity.
	private CellStyle createDateCellStyle(Workbook workbook) {
		CellStyle style = createDataStyle(workbook); // Inherit base data style
		// MODIFICATION: Setting the cell's data format is crucial for Excel to recognize it as a date.
		CreationHelper createHelper = workbook.getCreationHelper();
		style.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
		style.setAlignment(HorizontalAlignment.CENTER);
		return style;
	}
	
	// MODIFICATION: New style for data cells that should be centered (e.g., ID, Sex, Age).
	private CellStyle createCenteredDataStyle(Workbook workbook) {
		CellStyle style = createDataStyle(workbook); // Inherit base data style
		style.setAlignment(HorizontalAlignment.CENTER);
		return style;
	}
	
	/**
	 * View individual request details
	 */
	@RequestMapping(value = "/module/sespct/viewRequest", method = RequestMethod.GET)
	public String viewRequest(@RequestParam("pedidoId") String pedidoId, ModelMap model) {
		
		try {
			PedidoService pedidoService = Context.getService(PedidoService.class);
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
	
	@RequestMapping(value = "test", method = RequestMethod.GET)
	public String test() {
		return "sespct/index";
	}
}
