package com.developer.service;

import com.developer.dto.MemberDTO;

public interface MemberService {
	int register(MemberDTO dto) throws Exception;
	
	int idOverlap(MemberDTO dto) throws Exception;
	
	int login(MemberDTO dto) throws Exception;

	int delete(MemberDTO dto) throws Exception;
}
