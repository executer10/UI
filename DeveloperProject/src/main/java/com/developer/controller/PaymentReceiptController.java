package com.developer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.developer.dto.PaymentReceiptDTO;
import com.developer.service.PaymentReceiptService;


@Controller
public class PaymentReceiptController {
	@Autowired
	PaymentReceiptService paymentReceiptService;

	@GetMapping("/details")
	public String details() {
		return "/detail/view_details";
	}
	
	@PostMapping("/payment")
	public String chargeList(PaymentReceiptDTO paymentReceiptDTO, Model model) throws Exception{
		List<PaymentReceiptDTO> result = paymentReceiptService.chargeList(paymentReceiptDTO);
	    
		if(result == null || result.isEmpty()) {
			model.addAttribute("msg", "요금 정보를 찾을 수 없습니다.");
	       	model.addAttribute("url", "/main");
	       	return "/member/alertPrint";
		}
		
		System.out.println(paymentReceiptDTO.getCustomer_name());
		System.out.println(paymentReceiptDTO.getContract_number());
		System.out.println(paymentReceiptDTO.getYear());
		model.addAttribute("PRDTO", result);
		return "/detail/PaymentReceipt";
	}
}
