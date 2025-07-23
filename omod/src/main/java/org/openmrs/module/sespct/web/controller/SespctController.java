package org.openmrs.module.sespct.web.controller;

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
	
	@RequestMapping(value = "test", method = RequestMethod.GET)
	public String test() {
		return "sespct/index";
	}
}
