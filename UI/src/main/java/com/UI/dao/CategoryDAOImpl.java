package com.UI.dao;
/*CategoryDAOImpl.java*/
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.UI.domain.CategoryDTO;

@Repository
public class CategoryDAOImpl implements CategoryDAO {

	@Autowired
	SqlSession session;

	// Mapper 파일 경로 설정
	String namespace = "com.UI.dao.CategoryMapper.";

	@Override
	public int insert(CategoryDTO categoryDTO) throws Exception {
		return session.insert(namespace + "insert", categoryDTO);
	}

	@Override
	public int delete(CategoryDTO categoryDTO) throws Exception {
		return session.delete(namespace + "delete", categoryDTO);
	}

	@Override
	public List<CategoryDTO> select(String c_name) throws Exception {
		return session.selectList(namespace + "select", c_name);
	}

}
