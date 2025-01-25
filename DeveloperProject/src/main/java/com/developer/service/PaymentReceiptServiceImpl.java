package com.developer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developer.dao.PaymentReceiptDAO;
import com.developer.dto.PaymentReceiptDTO;

@Service
public class PaymentReceiptServiceImpl implements PaymentReceiptService {

	@Autowired
	PaymentReceiptDAO paymentReceiptDAO;
	
	@Override
	public List<PaymentReceiptDTO> chargeList(PaymentReceiptDTO paymentReceiptDTO) throws Exception {
		return paymentReceiptDAO.chargeList(paymentReceiptDTO);
	}

}
