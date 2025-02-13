package com.example.demo.service;

import com.example.demo.dto.MemberDTO;

public interface MemberService {

	int register(MemberDTO dto) throws Exception;

	int idOverlap(MemberDTO dto) throws Exception;

	int login(MemberDTO dto) throws Exception;

	MemberDTO getUserById(String userId) throws Exception;

	int memberUpdate(MemberDTO currentUser, String newPassword) throws Exception;

	int delete(String userId) throws Exception;
}
