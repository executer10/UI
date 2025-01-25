package com.developer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developer.dao.MemberDAO;
import com.developer.dto.MemberDTO;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	MemberDAO memberDAO;
	
	@Override
	public int register(MemberDTO dto) throws Exception {
		return memberDAO.register(dto);
	}

	@Override
	public int idOverlap(MemberDTO dto) throws Exception {
		return memberDAO.idOverlap(dto);
	}

	@Override
	public int login(MemberDTO dto) throws Exception {
		return memberDAO.login(dto);
	}

	@Override
	public int delete(MemberDTO dto) throws Exception {
		return memberDAO.delete(dto);
	}

}
