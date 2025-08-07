package org.openmrs.module.sespct.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.SESPCTService;
import org.openmrs.module.sespct.api.model.SESPCTRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/module/sespct/")
public class SespctController {
	
	private static final Logger log = LoggerFactory.getLogger(SespctController.class);
	
	@RequestMapping(value = "sespct.form", method = RequestMethod.GET)
	public String showMainPage(ModelMap model, @RequestParam(value = "estado", required = false) String estado) {
		
		try {
			SESPCTService sespctService = Context.getService(SESPCTService.class);
			
			List<SESPCTRequest> requests;
			if (estado != null && !estado.trim().isEmpty()) {
				requests = sespctService.getSESPCTRequestsByEstado(estado);
			} else {
				requests = sespctService.getAllSESPCTRequests();
			}
			
			model.addAttribute("sespctRequests", requests);
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
	@RequestMapping(value = "/module/sespct/manageftcases/export", method = RequestMethod.GET)
	public String exportCases(ModelMap model) {
		// TODO: Implement export functionality
		return "redirect:/module/sespct/sespct.form";
	}
	
	/**
	 * View individual request details
	 */
	@RequestMapping(value = "/module/sespct/viewRequest", method = RequestMethod.GET)
	public String viewRequest(@RequestParam("pedidoId") String pedidoId, ModelMap model) {
		
		try {
			SESPCTService sespctService = Context.getService(SESPCTService.class);
			SESPCTRequest request = sespctService.getSESPCTRequestByPedidoId(pedidoId);
			
			if (request != null) {
				model.addAttribute("sespctRequest", request);
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
	@RequestMapping(value = "manageftcases/export.form", method = RequestMethod.GET)
	public void downloadExcel(HttpServletResponse response) throws IOException {

		try {
			String content = "US,NID,NCFT\nUS Maputo,123456,NCFT001\n";

			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=test.csv");

			response.getWriter().write(content);
			response.getWriter().flush();

		}
		catch (Exception e) {
			log.error("Error", e);
		}
	}

	@RequestMapping(value = "test", method = RequestMethod.GET)
	public String test() {
		return "sespct/index";
	}
}
