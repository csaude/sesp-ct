package org.openmrs.module.sespct.api.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openmrs.module.sespct.api.ExportService;
import org.openmrs.module.sespct.api.dao.impl.PedidoDaoImpl;
import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.model.Resposta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ExcelExportServiceImpl implements ExportService {
	
	private static final Logger log = LoggerFactory.getLogger(PedidoDaoImpl.class);
	
	@Override
    public byte[] generatePedidoReport(List<Pedido> pedidos, String startDate, String endDate) throws IOException {
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

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			String formattedStartDate = startDate; // Default to original in case of error
			String formattedEndDate = endDate;   // Default to original in case of error

			try {
				// 2. Parse the input strings into date objects
				LocalDate start = LocalDate.parse(startDate, inputFormatter);
				LocalDate end = LocalDate.parse(endDate, inputFormatter);

				// 3. Format the date objects into the new string format
				formattedStartDate = start.format(outputFormatter);
				formattedEndDate = end.format(outputFormatter);
			} catch (DateTimeParseException e) {
				// Log an error if the dates are in an unexpected format, but don't crash
				log.error("Could not parse report dates for formatting: " + startDate + ", " + endDate, e);
			}

            Cell periodValueCell = periodRow.createCell(2);
            periodValueCell.setCellValue(formattedStartDate + " - " + formattedEndDate);
            periodValueCell.setCellStyle(subtitleValueStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 4)); // Merge C2:E2


            // Row 3: Headers (previously row 2)
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.setHeightInPoints(20); // MODIFICATION: Increased header row height for wrapped text.
            String[] headers = {"US", "NID", "NCFT", "Iniciais", "Sexo", "Idade", "Gestante", "Lactante", "Submissão", "Data de resposta",
                    "Sincronização", "Estado", "Causa de Não processamento", "Linha Terap. (resposta)", "Esquema (resposta)", "Solicitante email",
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
		
		Resposta ultimaResposta = null;
		if (pedido.getRespostas() != null && !pedido.getRespostas().isEmpty()) {
			ultimaResposta = pedido.getRespostas().get(pedido.getRespostas().size() - 1);
		}
		
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
		
		// Gestante
		String gestanteValue = pedido.getDadosUtente() != null ? pedido.getDadosUtente().getGestante() : "";
		String gestante = "nao".equals(gestanteValue) ? "Não" : ("sim".equals(gestanteValue) ? "Sim" : pedido
		        .getDadosUtente().getGestante());
		createCell(row, colNum++, gestante, centeredDataStyle);
		
		// Lactante
		String lactanteValue = pedido.getDadosUtente() != null ? pedido.getDadosUtente().getLactante() : "";
		String lactante = "nao".equals(lactanteValue) ? "Não" : ("sim".equals(lactanteValue) ? "Sim" : pedido
		        .getDadosUtente().getLactante());
		
		createCell(row, colNum++, lactante, centeredDataStyle);
		
		// Submissão
		createDateCell(row, colNum++, pedido.getDataSubmissao(), dateCellStyle);
		
		// Data de resposta
		LocalDateTime dataResposta = (ultimaResposta != null) ? ultimaResposta.getDataResposta() : null;
		if (dataResposta != null) {
			createDateCell(row, colNum++, dataResposta, dateCellStyle);
		} else {
			createCell(row, colNum++, "-", centeredDataStyle);
		}
		
		// Sincronização
		LocalDateTime dataSincronizacao = (ultimaResposta != null) ? ultimaResposta.getTimestamp() : pedido
		        .getDataSubmissao();
		createDateCell(row, colNum++, dataSincronizacao, dateCellStyle);
		
		// Estado
		String estado = pedido.getEstado() != null ? pedido.getEstado() : "";
		if (Pedido.ESTADO_SEM_RESPOSTA.equals(estado) || "No Response".equals(estado)) {
			estado = "Sem resposta";
		} else if (Pedido.ESTADO_NAO_PROCESSADO.equals(estado) || "Not Processed".equals(estado)) {
			estado = "Não Processado";
		} else if (Pedido.ESTADO_APROVADO.equals(estado) || "Approved".equals(estado)) {
			estado = "Aprovado";
		} else if (Pedido.ESTADO_ADIADO.equals(estado) || "Deferred".equals(estado)) {
			estado = "Adiado";
		}
		createCell(row, colNum++, estado, dataStyle);
		
		// Causa de Não processamento
		String causa = "-";
		if (Pedido.ESTADO_NAO_PROCESSADO.equals(pedido.getEstado()) || "Not Processed".equals(pedido.getEstado())) {
			causa = " NID não encontrado";
		}
		createCell(row, colNum++, causa, dataStyle);
		
		// Linha Terap. (resposta)
		String linhaTerapeutica = "";
		if (ultimaResposta != null) {
			String valor = ultimaResposta.getLinhaTerapeutica();
			if (valor != null && !valor.isEmpty()) {
				linhaTerapeutica = valor.replace("_linha", " Linha");
			}
		}
		createCell(row, colNum++, linhaTerapeutica, dataStyle);
		
		// Esquema (resposta)
		String esquema = "";
		if (ultimaResposta != null) {
			esquema = ultimaResposta.getEsquemaAprovado();
		}
		createCell(row, colNum++, esquema, dataStyle);
		
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
		if (ultimaResposta != null) {
			aprovadorEmail = ultimaResposta.getEmailAutorizante();
		}
		createCell(row, colNum++, aprovadorEmail, dataStyle);
	}
	
	private void createCell(Row row, int colNum, String value, CellStyle style) {
		Cell cell = row.createCell(colNum);
		cell.setCellValue(value != null ? value : "");
		cell.setCellStyle(style);
	}
	
	private void createDateCell(Row row, int colNum, LocalDateTime value, CellStyle style) {
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
}
