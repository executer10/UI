package com.developer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.developer.dto.BillingDTO;
import com.developer.service.BillingService;

@Controller
public class MainController {
	
	@Autowired
	BillingService billingService;
	
	@GetMapping("/main")
	public String main() {
		return "/main/main";
	}
	
	@PostMapping("/charge")
	public String charge(BillingDTO billingDTO, Model model) throws Exception {

		BillingDTO result = billingService.charge(billingDTO);

		if(result == null) {
			model.addAttribute("msg", "요금 정보를 찾을 수 없습니다.");
	       	model.addAttribute("url", "/main");
	       	return "/member/alertPrint";
		}
			model.addAttribute("billingData", result);
			
	        System.out.println(billingDTO.getContract_number());
	        System.out.println(billingDTO.getYear());
	        System.out.println(billingDTO.getMonth());
	        return "/main/main";
		}
}
