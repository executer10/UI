package com.developer.service;

import java.util.List;

import com.developer.dto.PaymentReceiptDTO;

public interface PaymentReceiptService {
	
	List<PaymentReceiptDTO> chargeList(PaymentReceiptDTO paymentReceiptDTO) throws Exception;
}
