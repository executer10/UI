package com.UI.user.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.UI.dao.CategoryDAO;
import com.UI.domain.CategoryDTO;
import com.UI.service.CategoryService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/root-context.xml" })
public class CategoryDAOTest {

	@Autowired
	private CategoryDAO categoryDAO;
	
	@Autowired
	private CategoryService categoryService;
	
	CategoryDTO categoryDTO;
	
	@Test
	public void insertTest() throws Exception {
		 
		categoryDTO = new CategoryDTO("HTML");
		assertTrue(categoryDAO.insert(categoryDTO) == 1);
		
	}

//	@Test
//	public void selectTest() throws Exception{
//		String c_name ="";
//		List<CategoryDTO> category = categoryService.select(c_name);
//		
//		// select 결과가 비어 있지 않은지 확인합니다.
//	    assertNotNull(category);
//	    assertFalse(category.isEmpty());
//		
//		for(CategoryDTO categoryDTO : category) {
//			System.out.println(categoryDTO.getC_name());
//		}
//		
//	}
}
