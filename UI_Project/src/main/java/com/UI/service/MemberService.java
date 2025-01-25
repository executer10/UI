package com.UI.service;

import com.UI.domain.MemberDTO;

public interface MemberService {
	int register(MemberDTO dto) throws Exception;
	
	int idOverlap(MemberDTO dto) throws Exception;
	
	int login(MemberDTO dto) throws Exception;

	int delete(String id) throws Exception;
	
	MemberDTO memberInfo(String id) throws Exception;
}
