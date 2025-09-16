package org.openmrs.module.sespct.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.sespct.api.PedidoService;
import org.openmrs.module.sespct.api.model.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.FlashMap;

@Controller
@RequestMapping("/module/sespct/")
@SessionAttributes({ "flashMessage" })
public class MapUnprocessedFtController {
	
	private PedidoService pedidoService;
	
	private MessageSourceService messageSourceService;
	
	private static final Logger log = LoggerFactory.getLogger(MapUnprocessedFtController.class);
	
	@Autowired
	public MapUnprocessedFtController(PedidoService pedidoService, MessageSourceService messageSourceService) {
		this.pedidoService = pedidoService;
		this.messageSourceService = messageSourceService;
	}
	
	@RequestMapping(value = "manageftcases/{id}/map.form", method = RequestMethod.GET)
	public String patientIdentifierMapping(@PathVariable Integer id, ModelMap model, HttpSession session,
	        HttpServletRequest request) {
		
		Pedido pedido = pedidoService.getPedidoById(id);
		
		// Load suggestions
		model.addAttribute("searchSuggestion", pedido.getDadosUtente().getIniciais());
		
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromServletMapping(request);
		
		if (session.getAttribute("lastSearchParams") != null) {
			@SuppressWarnings("unchecked")
			MultiValueMap<String, String> params = (MultiValueMap<String, String>) session.getAttribute("lastSearchParams");
			builder.queryParams(params);
		}
		String searchUri = builder.pathSegment("module", "sespct", "sespct.form").build().toUriString();
		
		model.addAttribute("lastSearchUri", searchUri);
		model.addAttribute("pedido", pedido);
		
		return "/module/sespct/sespct/map";
	}
	
	@RequestMapping(value = "manageftcases/{pedidoId}/map.form", method = RequestMethod.POST)
	public String mapPatientIdentifier(@PathVariable("pedidoId") Integer pedidoId, @RequestParam String patientUuid,
	        @RequestParam(required = false) String search, ModelMap model, HttpServletRequest request, HttpSession session) {
		
		// Get last search parameters from session for redirect
		@SuppressWarnings("unchecked")
		MultiValueMap<String, String> lastSearchParams = (MultiValueMap<String, String>) session
		        .getAttribute("lastSearchParams");
		String query = "";
		if (lastSearchParams != null) {
			query = ServletUriComponentsBuilder.fromCurrentRequest().queryParams(lastSearchParams).build().getQuery();
			if (query != null && !query.isEmpty()) {
				query = "?" + query;
			}
		}
		
		// Input validation
		if (pedidoId == null) {
			log.error("Pedido ID cannot be null");
			model.addAttribute("flashMessage",
			    messageSourceService.getMessage("sespct.error.invalid.pedido", null, Context.getLocale()));
			return "redirect:/module/sespct/sespct.form" + query;
		}
		
		if (StringUtils.isBlank(patientUuid)) {
			log.error("Patient UUID cannot be null or empty");
			model.addAttribute("flashMessage",
			    messageSourceService.getMessage("sespct.error.invalid.patient", null, Context.getLocale()));
			return "redirect:/module/sespct/sespct.form" + query;
		}
		
		try {
			Pedido pedido = pedidoService.getPedidoById(pedidoId);
			if (pedido == null) {
				log.error("Pedido not found with ID: {}", pedidoId);
				model.addAttribute("flashMessage",
				    messageSourceService.getMessage("sespct.error.pedido.not.found", null, Context.getLocale()));
				return "redirect:/module/sespct/sespct.form" + query;
			}
			
			Patient mappedPatient = pedidoService.mapIdentifier(patientUuid, pedido);
			
			// Safe attribute access with null checks
			String nid = pedido.getDadosUtente() != null ? pedido.getDadosUtente().getNid() : "Unknown";
			String patientIdentifier = mappedPatient.getPatientIdentifier() != null ? mappedPatient.getPatientIdentifier()
			        .getIdentifier() : "Unknown";
			
			String[] args = new String[] { nid, patientIdentifier };
			String successMessage = messageSourceService.getMessage("sespct.map.successful", args, Context.getLocale());
			
			// Use model attribute for success message with URL parameter approach
			model.addAttribute("flashMessage", successMessage);
			
			log.info("Successfully mapped NID {} to patient UUID {}", nid, patientUuid);
			return "redirect:/module/sespct/sespct.form" + query;
			
		}
		catch (IllegalArgumentException e) {
			log.error("Invalid argument for mapping: {}", e.getMessage());
			model.addAttribute("flashMessage",
			    messageSourceService.getMessage("sespct.error.invalid.argument", null, Context.getLocale()));
			return "redirect:/module/sespct/sespct.form" + query;
			
		}
		catch (IllegalStateException e) {
			log.error("Invalid state for mapping: {}", e.getMessage());
			model.addAttribute("flashMessage",
			    messageSourceService.getMessage("sespct.error.invalid.state", null, Context.getLocale()));
			return "redirect:/module/sespct/sespct.form" + query;
		}
		
		catch (Exception e) {
			log.error("Unexpected error during NID mapping", e);
			model.addAttribute("flashMessage",
			    messageSourceService.getMessage("sespct.error.unexpected", null, Context.getLocale()));
			return "redirect:/module/sespct/sespct.form" + query;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void restoreLastSearchParams(ModelMap model) {
		if (model.containsAttribute("lastSearchParams")) {
			try {
				Map<String, String> lastSearchParams = (Map<String, String>) model.get("lastSearchParams");
				if (lastSearchParams != null) {
					model.addAllAttributes(lastSearchParams);
				}
			}
			catch (ClassCastException e) {
				log.warn("Failed to restore last search parameters due to type mismatch", e);
			}
		}
	}
	
	@ModelAttribute("pageTitle")
	private void setPageTitle(ModelMap model) {
		String openMrs = messageSourceService.getMessage("openmrs.title", null, Context.getLocale());
		String pageTitle = messageSourceService.getMessage("sespct.map.identifiers", null, Context.getLocale());
		model.addAttribute("pageTitle", openMrs + " - " + pageTitle);
	}
}
