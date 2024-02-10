package com.UI.service;
/*BoardService.java*/
import java.util.List;
import java.util.Map;

import com.UI.domain.BoardDTO;
import com.UI.domain.SearchCondition;

public interface BoardService {

	BoardDTO select(int bno) throws Exception;

	int count(String c_name) throws Exception;

	int insert(BoardDTO boardDTO) throws Exception;

	int update(BoardDTO boardDTO) throws Exception;

	int delete(int bno, String writer) throws Exception;

	int deleteForAdmin(int bno) throws Exception;

	int deleteAll(BoardDTO boardDTO) throws Exception;

	List<BoardDTO> selectAll() throws Exception;

	BoardDTO increaseViewCnt(int bno) throws Exception;

	List<BoardDTO> getPage(Map map) throws Exception;

	List<BoardDTO> getSearchSelectPage(SearchCondition sc) throws Exception;

	int getSearchResultCnt(SearchCondition sc) throws Exception;
	
	List<BoardDTO> getCategoryList(String c_name) throws Exception;
}