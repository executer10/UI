package com.developer.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.developer.dto.PaymentReceiptDTO;

public class PaymentReceiptDAOImpl implements PaymentReceiptDAO {

	@Autowired
	SqlSession session;
	
	String namespace = "com.developer.dao.PaymentReceiptDAO.";
	
	@Override
	public List<PaymentReceiptDTO> chargeList(PaymentReceiptDTO paymentReceiptDTO) throws Exception {
	
		return session.selectList(namespace + "chargeList", paymentReceiptDTO);
	}

}
