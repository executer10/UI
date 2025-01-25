package com.developer.dto;

public class PaymentReceiptDTO {
	// 요청 필드
    private String customer_name;
    private long contract_number;
    private int year;
    
    // 응답 필드 
    private String customer_phone;
    private String billing_date;
    private int billed_amount;
    private int paid_amount;
    
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public long getContract_number() {
		return contract_number;
	}
	public void setContract_number(long contract_number) {
		this.contract_number = contract_number;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getCustomer_phone() {
		return customer_phone;
	}
	public void setCustomer_phone(String customer_phone) {
		this.customer_phone = customer_phone;
	}
	public String getBilling_date() {
		return billing_date;
	}
	public void setBilling_date(String billing_date) {
		this.billing_date = billing_date;
	}
	public int getBilled_amount() {
		return billed_amount;
	}
	public void setBilled_amount(int billed_amount) {
		this.billed_amount = billed_amount;
	}
	public int getPaid_amount() {
		return paid_amount;
	}
	public void setPaid_amount(int paid_amount) {
		this.paid_amount = paid_amount;
	}
	@Override
	public String toString() {
		return "PaymentReceiptDTO [customer_name=" + customer_name + ", contract_number=" + contract_number + ", year="
				+ year + ", customer_phone=" + customer_phone + ", billing_date=" + billing_date + ", billed_amount="
				+ billed_amount + ", paid_amount=" + paid_amount + "]";
	}
    
	
}
