package org.openmrs.module.sespct.web.controller;

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

@Controller
@RequestMapping("/module/sespct/")
public class SespctController {
	
	private static final Logger log = LoggerFactory.getLogger(SespctController.class);
	
	@RequestMapping(value = "sespct.form", method = RequestMethod.GET)
	public String search(ModelMap model) {
		model.addAttribute("user", "Hello Shaquil Hanif!");
		
		return "/module/sespct/sespct/index";
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
