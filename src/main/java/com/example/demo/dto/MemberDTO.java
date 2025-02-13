package com.example.demo.dto;

import java.util.Objects;

public class MemberDTO {
	private String userId;
	private String userPw;
	private String email;
	private String name;
	private String phoneNumber;
	private String postcode;
	private String address;
	private String addressDetail;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserPw() {
		return userPw;
	}
	public void setUserPw(String userPw) {
		this.userPw = userPw;
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
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddressDetail() {
		return addressDetail;
	}
	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}
	@Override
	public int hashCode() {
		return Objects.hash(address, addressDetail, email, name, phoneNumber, postcode, userId, userPw);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemberDTO other = (MemberDTO) obj;
		return Objects.equals(address, other.address) && Objects.equals(addressDetail, other.addressDetail)
				&& Objects.equals(email, other.email) && Objects.equals(name, other.name)
				&& Objects.equals(phoneNumber, other.phoneNumber) && Objects.equals(postcode, other.postcode)
				&& Objects.equals(userId, other.userId) && Objects.equals(userPw, other.userPw);
	}
	@Override
	public String toString() {
		return "MemberDTO [userId=" + userId + ", userPw=" + userPw + ", email=" + email + ", name=" + name
				+ ", phoneNumber=" + phoneNumber + ", postcode=" + postcode + ", address=" + address
				+ ", addressDetail=" + addressDetail + "]";
	}
 
}
