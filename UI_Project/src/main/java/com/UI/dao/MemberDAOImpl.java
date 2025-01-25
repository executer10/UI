package com.UI.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.UI.domain.MemberDTO;

public class MemberDAOImpl implements MemberDAO {

	@Autowired
	SqlSession session;
	
	String namespace = "com.UI.dao.MemberDAO.";
	
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
	public int delete(String id) throws Exception {

		return session.delete(namespace + "delete", id);
	}

	@Override
	public MemberDTO memberInfo(String id) throws Exception {
		return session.selectOne(namespace + "memberInfo", id);
	}

}
