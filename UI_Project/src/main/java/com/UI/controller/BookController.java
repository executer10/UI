package com.UI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookController {
	
	@GetMapping("/HTML/CommandCenter")
	public String HTML_CommandCenter() {
		
		return "CommandCenter/HTML_CommandCenter";
	}
	
	@GetMapping("/CSS/CommandCenter")
	public String CSS_CommandCenter() {
		
		return "CommandCenter/CSS_CommandCenter";
	}
	
	@GetMapping("/JS/CommandCenter")
	public String JS_CommandCenter() {
		
		return "CommandCenter/JS_CommandCenter";
	}
	
	@GetMapping("/Java/CommandCenter")
	public String Java_CommandCenter() {
		
		return "CommandCenter/Java_CommandCenter";
	}
	
	@GetMapping("/Python/CommandCenter")
	public String Python_CommandCenter() {
		
		return "CommandCenter/Python_CommandCenter";
	}
	
	@GetMapping("/MySQL/CommandCenter")
	public String MySQL_CommandCenter() {
		
		return "CommandCenter/MySQL_CommandCenter";
	}
	
	@GetMapping("/JSP/CommandCenter")
	public String JSP_CommandCenter() {
		
		return "CommandCenter/JSP_CommandCenter";
	}
	
	@GetMapping("/Spring/CommandCenter")
	public String Spring_CommandCenter() {
		
		return "CommandCenter/Spring_CommandCenter";
	}
	
	@GetMapping("/Github/CommandCenter")
	public String Github_CommandCenter() {
		
		return "CommandCenter/Github_CommandCenter";
	}
}
