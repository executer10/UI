package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/main")
	public String main() {
		return "main";
	}

	@GetMapping("/languages")
	public String languages() {
		return "languages";
	}

	@GetMapping("/languages/HTML")
	public String HTML_languages() {

		return "languages/HTML_languages";
	}

	@GetMapping("/languages/CSS")
	public String CSS_languages() {

		return "languages/CSS_languages";
	}

	@GetMapping("/languages/JS")
	public String JS_languages() {

		return "languages/JS_languages";
	}

	@GetMapping("/languages/Java")
	public String Java_languages() {

		return "languages/Java_languages";
	}

	@GetMapping("/languages/Python")
	public String Python_languages() {

		return "languages/Python_languages";
	}

	@GetMapping("/languages/MySQL")
	public String MySQL_languages() {

		return "languages/MySQL_languages";
	}

	@GetMapping("/languages/JSP")
	public String JSP_languages() {

		return "languages/JSP_languages";
	}

	@GetMapping("/languages/Spring")
	public String Spring_languages() {

		return "languages/Spring_languages";
	}

	@GetMapping("/languages/Github")
	public String Github_languages() {

		return "languages/Github_languages";
	}

}
