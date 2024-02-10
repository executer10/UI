package com.UI.dao;
/*UserDAO.java*/
import com.UI.domain.UserDTO;

public interface UserDAO {
	// 코드가 동작을 하는지 확인하는 int
	// 0인 경우 작업이 실패했음
	// 1인경우 작업이 성공했음
	int register(UserDTO userDTO) throws Exception;

	int idOverlap(UserDTO userDTO) throws Exception;

	int login(UserDTO userDTO) throws Exception;

	int delete(UserDTO userDTO) throws Exception;
}
