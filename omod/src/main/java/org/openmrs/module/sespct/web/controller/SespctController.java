package org.openmrs.module.sespct.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.SESPCTService;
import org.openmrs.module.sespct.api.model.SESPCTRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

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
	}
}
