package com.UI.service;
/*CategoryServiceImpl.java*/
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.UI.dao.CategoryDAO;
import com.UI.domain.CategoryDTO;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	CategoryDAO categoryDAO;

	@Override
	public int insert(CategoryDTO categoryDTO) throws Exception {
		return categoryDAO.insert(categoryDTO);
	}

	@Override
	public int delete(CategoryDTO categoryDTO) throws Exception {
		return categoryDAO.delete(categoryDTO);
	}

	@Override
	public List<CategoryDTO> select(String c_name) throws Exception {
		return categoryDAO.select(c_name);
	}

}
