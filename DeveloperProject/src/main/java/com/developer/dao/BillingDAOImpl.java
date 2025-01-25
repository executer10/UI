package com.developer.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.developer.dto.BillingDTO;

public class BillingDAOImpl implements BillingDAO {

	@Autowired
	SqlSession session;
	
	String namespace = "com.developer.dao.BillingDAO.";

	@Override
    public BillingDTO charge(BillingDTO billingDTO) throws Exception {
        return session.selectOne(namespace + "charge", billingDTO);
    }
}