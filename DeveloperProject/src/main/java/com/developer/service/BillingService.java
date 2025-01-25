package com.developer.service;

import com.developer.dto.BillingDTO;

public interface BillingService {
	BillingDTO charge(BillingDTO billingDTO) throws Exception;
}
