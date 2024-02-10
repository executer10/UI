package com.UI.controller;

/*BoardController.java*/
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.UI.domain.BoardDTO;
import com.UI.domain.CategoryDTO;
import com.UI.domain.PageHandler;
import com.UI.domain.SearchCondition;
import com.UI.service.BoardService;
import com.UI.service.CategoryService;

/* Spring MVC의 컨트롤러로 선언합니다. */
@Controller
public class BoardController {

	/* BoardService와 CategoryService에 대한 의존성을 주입합니다. */
	@Autowired
	BoardService boardService;
	
	@Autowired
	CategoryService categoryService;
	
	/* 게시판 목록을 보여주는 메서드입니다. */
	@GetMapping("/boardList")
	public String CategoryBoard(@RequestParam("category") String c_name, SearchCondition sc, Model model, HttpServletRequest request) {
		
		// 로그인 체크를 하여 로그인하지 않았다면 로그인 페이지로 리다이렉트합니다.
		if(!loginCheck(request)) {
			return "redirect:/login/login?toURL="+request.getRequestURL();
		}
		try {
			// 선택한 카테고리에 해당하는 게시글 수를 가져옵니다.
			int totalCnt = boardService.count(c_name);
			
			// 페이지 처리를 위한 PageHandler 객체를 생성합니다.
			PageHandler pageHandler = new PageHandler(totalCnt, sc);
			
			// 검색 조건을 설정합니다.
			sc.setC_name(c_name);
			
			// 조건에 맞는 게시글 리스트를 가져옵니다.
			List<BoardDTO> searchCondition = boardService.getSearchSelectPage(sc);

			// 카테고리 리스트를 가져옵니다.
			List<CategoryDTO> categoryList = categoryService.select(c_name);
			
			// 모델에 게시글 리스트와 페이지 핸들러를 추가합니다.
			model.addAttribute("list", searchCondition);
			model.addAttribute("ph", pageHandler);
			
			// 오늘의 시작 시간을 밀리초로 변환하여 모델에 추가합니다.
			Instant startOfToday=LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
			model.addAttribute("startOfToday", startOfToday.toEpochMilli());
			
			// 선택한 카테고리가 실제로 존재하는지 확인합니다.
			boolean categoryExists=false;
			for(CategoryDTO categoryDTO : categoryList) {
				if (categoryDTO.getC_name().equals(c_name)) {
					categoryExists = true;
	                break;
				 }
			}
			
			// 카테고리 이름을 모델에 추가합니다.
			model.addAttribute("c_name", c_name);
			
			// 카테고리가 존재하면 게시판 목록 페이지를, 그렇지 않으면 언어 선택 페이지로 리다이렉트합니다.
			if (categoryExists == true) {
	            model.addAttribute("categoryList", categoryList);
	            return "boardList";
			}
			else {
				return "redirect:/boardList/languageSelection";
			}
		} catch (Exception e) {
			// 오류 처리를 합니다.
			model.addAttribute("msg", "LIST_ERR");
			model.addAttribute("totalCnt", 0);
			e.printStackTrace();
			return "redirect:/boardList/languageSelection";
		}
		
	}
	
	/* 게시글을 읽는 메서드입니다. */
	@GetMapping("/board/read")
	public String read(int bno, int page, int pageSize, Model model) {
		
		try {
			// 게시글 번호로 게시글을 조회합니다.
			BoardDTO boardDTO = boardService.select(bno);
			
			//조회수 증가
			boardService.increaseViewCnt(bno);
			
			// 모델에 게시글 정보와 페이지 정보를 추가합니다.
			model.addAttribute("boardDTO",boardDTO);
			model.addAttribute("page", page);
			model.addAttribute("pageSize", pageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "board";
	}
	
	/* 게시글 작성 페이지로 이동하는 메서드입니다. */
	@GetMapping("/board/write")
	public String write(Model m) {
		
		// 모델에 'new'라는 모드를 추가하여, 새 게시글 작성임을 나타냅니다.
		m.addAttribute("mode", "new");
		
		// board 뷰를 반환합니다.
		return "board";
	}
	
	/* 게시글 작성 요청을 처리하는 메서드입니다. */
	@PostMapping("/board/write")
	public String write(BoardDTO boardDTO, RedirectAttributes redirectAttributes, Model m, HttpSession session) {
		
		// 세션에서 작성자의 ID를 가져와 boardDTO에 설정합니다.
		String writer = (String) session.getAttribute("id");
		String c_name = boardDTO.getC_name();
		boardDTO.setWriter(writer);
		try {
			// 게시글을 데이터베이스에 저장하고, 성공 여부에 따라 처리합니다.
			if(boardService.insert(boardDTO)!=1) {
				
				// 게시글 저장에 실패하면 예외를 발생시킵니다.
				throw new Exception("Write failed");
			}

			// 성공 메시지를 리다이렉트 속성에 추가합니다.
			redirectAttributes.addFlashAttribute("msg", "WRT_OK");
			
			// 게시글 목록 페이지로 리다이렉트합니다.
			return "redirect:/boardList?category=" + c_name;
		} catch (Exception e) {
			e.printStackTrace();
			
			// 예외 발생 시 오류 메시지와 함께 다시 작성 페이지로 이동합니다.
			m.addAttribute("mode","new"); //글쓰기 모드로 이동
			m.addAttribute("boardDTO", boardDTO);//등록하려던 내용 전송
			m.addAttribute("msg", "WRT_ERR");//에러 메세지
			
			return "board";
		}
	}
	
	/* 게시글 삭제 요청을 처리하는 메서드입니다. */
	@PostMapping("/board/delete")
	public String delete(BoardDTO boardDTO, int page, int pageSize, RedirectAttributes redirectAttributes, Model model, HttpSession session) {
		
		// 세션에서 작성자의 ID를 가져옵니다.
		String writer = (String) session.getAttribute("id");
		
		// 게시글의 카테고리 이름을 가져옵니다.
		String c_name = boardDTO.getC_name();
		
		// 삭제할 게시글의 번호를 가져옵니다.
		int bno = boardDTO.getBno();
		
		try {
			
			model.addAttribute("page", page);
			model.addAttribute("pageSize", pageSize);
			
			// 게시글을 데이터베이스에서 삭제하고, 결과 행의 수를 반환받습니다.
			int rowCnt = boardService.delete(bno, writer);
			if(rowCnt !=1) {
				
				// 삭제된 행이 1이 아니라면, 예상치 못한 상황으로 간주하고 예외를 발생시킵니다.
				throw new Exception("remove error");
			}
			
			//redirectAttributes.addFlashAttribute : 1회용
			//세션에 메세지를 저장하여 전달. 한번 사용하고 나면 알아서 삭제됨.
			redirectAttributes.addFlashAttribute("msg", "DEL_OK");
		}catch (Exception e) {
			
			// 예외가 발생했다면 오류 메시지를 설정합니다.
			model.addAttribute("msg","DEL_ERR");
			e.printStackTrace();
		}
		// 삭제 후 게시글 목록 페이지로 리다이렉트합니다. 카테고리와 페이지 정보를 URL에 포함시킵니다.
		return "redirect:/boardList?category=" + c_name;
	}
	
	/* 게시글 작성 페이지로 이동하는 메서드입니다. */
	@GetMapping("/board/update")
	public String modify(Model m) {
		
		// 모델에 'new'라는 모드를 추가하여, 새 게시글 작성임을 나타냅니다.
		m.addAttribute("mode", "modify");
		
		// board 뷰를 반환합니다.
		return "board";
	}
	/* 게시글 수정 요청을 처리하는 메서드입니다. */
	@PostMapping("/board/update")
	public String modify(BoardDTO boardDTO, RedirectAttributes rattr, Model m , HttpSession session) {
		
		// 세션에서 로그인한 사용자 아이디를 가져옵니다.
		String writer = (String)session.getAttribute("id");
		
		// 가져온 사용자 아이디를 BoardDTO에 세팅합니다.
		boardDTO.setWriter(writer);
		
		// BoardDTO에서 카테고리 이름을 가져옵니다.
		String c_name = boardDTO.getC_name();
		
		try {
			// BoardService를 이용하여 게시글을 수정합니다.
			if(boardService.update(boardDTO) != 1) {
				// 수정에 실패하면 예외를 발생시킵니다.
				throw new Exception("modify failed");
			}
			// RedirectAttributes에 수정 성공 메시지를 추가합니다.
			rattr.addAttribute("msg", "MOD_OK");
			
			// 게시글 목록으로 리다이렉트 합니다.
			return "redirect:/boardList?category=" + c_name;
			
		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("mode", "new");//글쓰기 모드로 이동
			m.addAttribute("boardDto", boardDTO);// 등록하려던 내용 전송
			m.addAttribute("msg", "MOD_ERR"); //에러 메세지
			return "board";//파일 위치를 찾아감
		}
	}

	// 로그인 여부를 확인하는 헬퍼 메서드입니다.
	private boolean loginCheck(HttpServletRequest request) {
			
		//1) 세션을 가져오기
		HttpSession session = request.getSession();

		//2) id 값이 null이 아닐 때 true 반환, null일 때 false 반환
		return session.getAttribute("id") != null;	
	}
}