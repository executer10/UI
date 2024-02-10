package com.UI.dao;
/*CategoryDAO.java*/
import java.util.List;

import com.UI.domain.CategoryDTO;

public interface CategoryDAO {

	int insert(CategoryDTO categoryDTO) throws Exception;

	int delete(CategoryDTO categoryDTO) throws Exception;

	List<CategoryDTO> select(String c_name) throws Exception;
}
