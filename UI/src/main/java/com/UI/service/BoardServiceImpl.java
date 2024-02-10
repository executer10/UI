package com.UI.service;
/*BoardServiceImpl.java*/
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.UI.dao.BoardDAO;
import com.UI.domain.BoardDTO;
import com.UI.domain.SearchCondition;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	BoardDAO boardDAO;

	@Override
	public BoardDTO select(int bno) throws Exception {
		return boardDAO.select(bno);
	}

	@Override
	public int count(String c_name) throws Exception {
		return boardDAO.count(c_name);
	}

	@Override
	public int insert(BoardDTO boardDTO) throws Exception {
		return boardDAO.insert(boardDTO);
	}

	@Override
	public int update(BoardDTO boardDTO) throws Exception {
		return boardDAO.update(boardDTO);
	}

	@Override
	public int delete(int bno, String writer) throws Exception {
		return boardDAO.delete(bno, writer);
	}

	@Override
	public int deleteForAdmin(int bno) throws Exception {
		return boardDAO.deleteForAdmin(bno);
	}

	@Override
	public int deleteAll(BoardDTO boardDTO) throws Exception {
		return boardDAO.deleteAll();
	}

	@Override
	public List<BoardDTO> selectAll() throws Exception {
		return boardDAO.selectAll();
	}

	@Override
	public BoardDTO increaseViewCnt(int bno) throws Exception {
		BoardDTO boardDTO = boardDAO.select(bno);
		boardDAO.increaseViewCnt(bno);
		return boardDTO;
	}

	@Override
	public List<BoardDTO> getPage(Map map) throws Exception {
		return boardDAO.selectPage(map);
	}

	@Override
	public List<BoardDTO> getSearchSelectPage(SearchCondition sc) throws Exception {
		return boardDAO.searchSelectPage(sc);
	}

	@Override
	public int getSearchResultCnt(SearchCondition sc) throws Exception {
		return boardDAO.searchResultCnt(sc);
	}

	@Override
	public List<BoardDTO> getCategoryList(String c_name) throws Exception {
	    return boardDAO.getCategoryList(c_name);
	}
}