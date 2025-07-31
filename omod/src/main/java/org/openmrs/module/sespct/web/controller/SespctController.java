package org.openmrs.module.sespct.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/module/sespct/")
public class SespctController {
	
	private static final Logger log = LoggerFactory.getLogger(SespctController.class);
	
	@RequestMapping(value = "sespct.form", method = RequestMethod.GET)
	public String search(ModelMap model) {
		model.addAttribute("user", "Hello Shaquil Hanif!");
		
		return "/module/sespct/sespct/index";
	}
	
	@RequestMapping(value = "manageftcases/export.form", method = RequestMethod.POST)
	public void String(HttpServletRequest request, HttpServletResponse response) {
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		
		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Start Date and End Date are required.");
			return;
		}
		log.info("Start Date: " + startDate + " End Date " + endDate);
	}
	
	@RequestMapping(value = "test", method = RequestMethod.GET)
	public String test() {
		return "sespct/index";
	}
}
