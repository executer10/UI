package com.UI.dao;
/*UserDAOImpl.java*/
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.UI.domain.UserDTO;

@Repository
public class UserDAOImpl implements UserDAO {

	// SqlSession은 MyBatis에서 데이터베이스와의 연결을 설정하고,
	// SQL 쿼리를 실행할 수 있는 인터페이스
	@Autowired
	SqlSession session;

	// Mapper 파일 경로 설정
	String namespace = "com.UI.dao.UserMapper.";

	@Override
	// register(): 사용자 등록(회원가입)
	public int register(UserDTO userDTO) throws Exception {
		return session.insert(namespace + "register", userDTO); // insert() 메소드는 쿼리에 의해 새로 생성된 레코드의 개수를 반환
	}

	@Override
	// idOverlap(): 사용자 ID 중복 여부를 확인
	public int idOverlap(UserDTO userDTO) throws Exception {
		return session.selectOne(namespace + "idOverlap", userDTO); // 존재하는 사용자 ID가 있으면 1을 반환하고, 없으면 0을 반환
	}

	@Override
	// login(): 사용자 로그인을 처리
	public int login(UserDTO userDTO) throws Exception {
		return session.selectOne(namespace + "login", userDTO); // 일치하는 사용자가 있으면 해당 값(예: 1)을 반환하고, 그렇지 않으면 0을 반환
	}

	@Override
	// delete(): 회원 탈퇴 처리
	public int delete(UserDTO userDTO) throws Exception {
		return session.delete(namespace + "delete", userDTO); // delete() 메소드는 삭제된 레코드의 개수를 반환합니다.
	}

}
