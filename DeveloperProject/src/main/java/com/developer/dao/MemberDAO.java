package com.developer.dao;

import com.developer.dto.MemberDTO;

public interface MemberDAO {
	int register(MemberDTO dto) throws Exception;
	
	int idOverlap (MemberDTO dto) throws Exception;
	
	int login(MemberDTO dto) throws Exception;

	int delete(MemberDTO dto) throws Exception; 
}
