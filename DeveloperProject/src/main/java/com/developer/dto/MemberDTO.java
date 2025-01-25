package com.developer.dto;

public class MemberDTO {
	private String user_id;
	private String password;
	private String email;
	private String name;
	private String phone_number;
	private String address;
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return "MemberDTO [user_id=" + user_id + ", password=" + password + ", email=" + email + ", name=" + name
				+ ", phone_number=" + phone_number + ", address=" + address + "]";
	}
	
	
	
	
}
