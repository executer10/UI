package com.UI.user.dao;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.UI.dao.UserDAO;
import com.UI.domain.UserDTO;

//@RunWith: 이 테스트가 SpringJUnit4ClassRunner를 사용하여 실행되어야 함을 나타냄. 
//SpringJUnit4ClassRunner: Spring에서 지원하는 JUnit 테스트 실행기로, 테스트 시 작성된 설정에 따라 빈(Bean)을 자동으로 로드하고 주입할 수 있다.
@RunWith(SpringJUnit4ClassRunner.class)

//@ContextConfiguration : 테스트가 실행되기 전에 사용할 애플리케이션 컨텍스트를 구성하는 방법을 지정.
//여기서는 file:src/main/webapp/WEB-INF/spring/root-context.xml 파일을 사용하도록 설정되어 있으며, 
//이 파일은 애플리케이션의 스프링 설정 파일이다.
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/root-context.xml" })
public class UserDAOTest {

	@Autowired
	private UserDAO dao;

	@Test
	public void insertTest() throws Exception {
		UserDTO dto = new UserDTO("green", "protoss10!", "킹그린", "202000101", "01012345678");
		assertTrue(dao.register(dto) == 1);
		assertTrue(dto.getName() == "킹그린");
	}
}
