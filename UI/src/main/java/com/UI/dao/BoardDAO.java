package com.UI.dao;
/*BoardDAO.java*/
import java.util.List;
import java.util.Map;

import com.UI.domain.BoardDTO;
import com.UI.domain.SearchCondition;

public interface BoardDAO {

	BoardDTO select(int bno) throws Exception;
	
	int count(String c_name) throws Exception;
	
	int insert(BoardDTO boardDTO) throws Exception;
	
	int update(BoardDTO boardDTO) throws Exception;
	
	int delete(int bno, String writer) throws Exception;
	
	int deleteForAdmin(int bno) throws Exception;
	
	int deleteAll() throws Exception;
	
	List<BoardDTO> selectAll() throws Exception;
	
	int increaseViewCnt(int bno) throws Exception;
	
	public List<BoardDTO> selectPage(Map map) throws Exception;
	
	public List<BoardDTO> searchSelectPage(SearchCondition sc) throws Exception;
		
	public int searchResultCnt(SearchCondition sc) throws Exception;

	int updateCommentsCnt(Integer bno, int cnt);
	
	List<BoardDTO> getCategoryList(String c_name) throws Exception;
}
