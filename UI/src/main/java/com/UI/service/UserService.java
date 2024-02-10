package com.UI.service;
/*UserService.java*/
import com.UI.domain.UserDTO;

public interface UserService {

	int register(UserDTO userDTO) throws Exception;
	
	int idOverlap(UserDTO userDTO) throws Exception;
	
	int login(UserDTO userDTO) throws Exception;
	
	int delete(UserDTO userDTO) throws Exception;
	
}
