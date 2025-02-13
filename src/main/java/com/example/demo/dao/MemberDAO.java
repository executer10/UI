package com.example.demo.dao;

import com.example.demo.dto.MemberDTO;

public interface MemberDAO {

	int register(MemberDTO dto) throws Exception;

	int idOverlap(MemberDTO dto) throws Exception;

	int delete(String userId) throws Exception;

	MemberDTO getUserById(String userId) throws Exception;

	int memberUpdate(MemberDTO currentUser) throws Exception;
}
