package com.developer.dto;

public class BillingDTO {
	private String contract_number;
	private String billing_date;
	private int usage_data;
	private int usage_fee;
	private int overdue_fee;
	private int vat_fee;
	private int total_amount;
	private int year;
    private int month;
	public String getContract_number() {
		return contract_number;
	}


	public void setContract_number(String contract_number) {
		this.contract_number = contract_number;
	}


	public String getBilling_date() {
		return billing_date;
	}


	public void setBilling_date(String billing_date) {
		this.billing_date = billing_date;
	}


	public int getUsage_data() {
		return usage_data;
	}


	public void setUsage_data(int usage_data) {
		this.usage_data = usage_data;
	}


	public int getUsage_fee() {
		return usage_fee;
	}


	public void setUsage_fee(int usage_fee) {
		this.usage_fee = usage_fee;
	}


	public int getOverdue_fee() {
		return overdue_fee;
	}


	public void setOverdue_fee(int overdue_fee) {
		this.overdue_fee = overdue_fee;
	}


	public int getVat_fee() {
		return vat_fee;
	}


	public void setVat_fee(int vat_fee) {
		this.vat_fee = vat_fee;
	}


	public int getTotal_amount() {
		return total_amount;
	}


	public void setTotal_amount(int total_amount) {
		this.total_amount = total_amount;
	}


	public int getYear() {
		return year;
	}


	public void setYear(int year) {
		this.year = year;
	}


	public int getMonth() {
		return month;
	}


	public void setMonth(int month) {
		this.month = month;
	}


	@Override
	public String toString() {
		return "BillingDTO [contract_number=" + contract_number + ", billing_date=" + billing_date + ", usage_data="
				+ usage_data + ", usage_fee=" + usage_fee + ", overdue_fee=" + overdue_fee + ", vat_fee=" + vat_fee
				+ ", total_amount=" + total_amount + ", year=" + year + ", month=" + month + "]";
	}
}
