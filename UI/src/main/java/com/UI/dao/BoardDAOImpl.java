package com.UI.dao;
/*BoardDAOImpl.java*/
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.UI.domain.BoardDTO;
import com.UI.domain.SearchCondition;

@Repository
public class BoardDAOImpl implements BoardDAO {

	@Autowired
	SqlSession session;
	
	String namespace = "com.UI.dao.BoardMapper.";
	
	@Override
	public BoardDTO select(int bno) throws Exception {
		return session.selectOne(namespace + "select", bno);
	}

	@Override
	public int count(String c_name) throws Exception {
		return session.selectOne(namespace + "count", c_name);
	}

	@Override
	public int insert(BoardDTO boardDTO) throws Exception {
		return session.insert(namespace + "insert", boardDTO);
	}

	@Override
	public int update(BoardDTO boardDTO) throws Exception {
		return session.update(namespace + "update", boardDTO);
	}

	@Override
	public int delete(int bno, String writer) throws Exception {
		Map map = new HashMap();
		map.put("bno", bno);
		map.put("writer", writer);
		return session.delete(namespace + "delete", map);
	}

	@Override
	public int deleteForAdmin(int bno) throws Exception {
		return session.delete(namespace + "deleteForAdmin", bno);
	}

	@Override
	public int deleteAll() throws Exception {
		return session.delete(namespace + "deleteAll");
	}

	@Override
	public List<BoardDTO> selectAll() throws Exception {
		return session.selectList(namespace + "selectAll");
	}

	@Override
	public int increaseViewCnt(int bno) throws Exception {
		return session.update(namespace + "increaseViewCnt", bno);
	}

	@Override
	public List<BoardDTO> selectPage(Map map) throws Exception {
		return session.selectList(namespace + "selectPage", map);
	}

	@Override
	public List<BoardDTO> searchSelectPage(SearchCondition sc) throws Exception {
		return session.selectList(namespace + "searchSelectPage", sc);
	}

	@Override
	public int searchResultCnt(SearchCondition sc) throws Exception {
		return session.selectOne(namespace + "searchResultCnt", sc);
	}

	@Override
	public int updateCommentsCnt(Integer bno, int cnt) {
		Map map = new HashMap();
		map.put("cnt", cnt);
		map.put("bno", bno);
		return session.update(namespace + "updateCommentsCnt", map);
	}

	@Override
	public List<BoardDTO> getCategoryList(String c_name) throws Exception {
	    return session.selectList(namespace + "getCategoryList", c_name);
	}

}
