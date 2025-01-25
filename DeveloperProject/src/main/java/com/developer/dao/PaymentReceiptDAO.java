package com.developer.dao;

import java.util.List;

import com.developer.dto.PaymentReceiptDTO;

public interface PaymentReceiptDAO {
	List<PaymentReceiptDTO>chargeList(PaymentReceiptDTO paymentReceiptDTO) throws Exception;
}
