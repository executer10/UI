package com.UI.dao;

import com.UI.domain.MemberDTO;

public interface MemberDAO {
	int register(MemberDTO dto) throws Exception;
	
	int idOverlap (MemberDTO dto) throws Exception;
	
	int login(MemberDTO dto) throws Exception;

	int delete(String id) throws Exception;
	
	MemberDTO memberInfo(String id) throws Exception;
}
