package com.developer.dao;

import com.developer.dto.BillingDTO;

public interface BillingDAO {
	BillingDTO charge(BillingDTO billingDTO) throws Exception;
}
