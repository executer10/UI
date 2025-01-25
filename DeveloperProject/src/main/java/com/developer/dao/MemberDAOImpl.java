package com.developer.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.developer.dto.MemberDTO;

public class MemberDAOImpl implements MemberDAO{
	
	@Autowired
	SqlSession session;
	
	String namespace = "com.developer.dao.MemberDAO.";

	@Override
	public int register(MemberDTO dto) throws Exception {
		return session.insert(namespace + "register", dto);
	}

	@Override
	public int idOverlap(MemberDTO dto) throws Exception {
		return session.selectOne(namespace + "idOverlap", dto);
	}

	@Override
	public int login(MemberDTO dto) throws Exception {
		return session.selectOne(namespace + "login", dto);
	}

	@Override
	public int delete(MemberDTO dto) throws Exception {
		return session.delete(namespace + "delete", dto);
	}
	
	
}
