package com.UI.user.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.UI.dao.BoardDAO;
import com.UI.domain.BoardDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/root-context.xml" })
public class BoardDAOTest {

	@Autowired
	BoardDAO boardDAO;
	
	@Autowired
	BoardDTO boardDTO;
	
	@Test
	public void countTest() throws Exception{

		String c_name = boardDTO.getC_name();
        int DAO = boardDAO.count(c_name);
        System.out.println(DAO);

	}
}
