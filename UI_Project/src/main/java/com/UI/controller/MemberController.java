package com.UI.controller;

import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.UI.domain.MemberDTO;
import com.UI.service.MemberService;

@Controller
public class MemberController {

	
	@Autowired
	MemberService memberService;
	
	@GetMapping("/index")
	public String GetIndex() {
		return "index";
	}
	
	@GetMapping("/join")
	public String GetJoin() {
		return "join";
	}
	
	@GetMapping("/login")
	public String GetLogin() {
		return "login";
	}
	
	@GetMapping("/member/page")
	public String GetMyPage(HttpSession session, Model model) {
		try {
			// 세션에서 아이디 가져오기
	        String id = (String) session.getAttribute("id");
	        
	        // 회원 정보 조회
	        MemberDTO memberInfo = memberService.memberInfo(id);
	        
	        // 모델에 회원 정보 저장
	        model.addAttribute("member", memberInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return "myPage";
	}
	
	// 아이디 중복 체크 메서드
	// 클라이언트로부터 받은 회원 정보의 아이디 중복 여부 확인
	// 중복되면 1, 중복되지 않으면 0 반환
	@ResponseBody
	@PostMapping("/join/idOverlap")
	public int idOverlap(MemberDTO memberDTO){
		try {
			int result = memberService.idOverlap(memberDTO);
			System.out.println("a");
	        return result;
	    } 
		catch (Exception e) {
	        e.printStackTrace();
	        return -1;  // 에러 발생 시 -1 반환
	    }
	}
	
	// 회원가입 처리 메서드
	// 1. 아이디 중복 체크
	// 2. 중복되지 않으면 회원 정보 등록
	// 3. 성공 시 alertPrint.jsp 뷰로 성공 메시지와 이동 URL 전달
	@PostMapping("/join/register")
	public String PostJoin(MemberDTO memberDTO, Model model) throws Exception{
		
		int result = idOverlap(memberDTO);
		
		// 아이디 중복 시 회원가입 페이지로 리다이렉트
		if(result != 0) {
			return "redirect:/join";
		}
		
		// 성공 메시지와 로그인 페이지 URL 설정
		model.addAttribute("msg", "회원 가입이 완료되었읍니다.");
		model.addAttribute("url", "/login");
		
		// 회원 정보 등록
		memberService.register(memberDTO);
		
		// alertPrint.jsp페이지 이동 후 /login주소로 이동
		return "alertPrint";
	}
	
	// 로그인 처리 메서드
	// 1. 로그인 정보 검증
	// 2. 로그인 성공 시 세션 및 쿠키 설정
	// 3. 지정된 URL 또는 메인 페이지로 리다이렉트
	@PostMapping("login/processing")
	public String login(MemberDTO memberDTO, String toURL, boolean rememberId, HttpServletResponse response, HttpServletRequest request) throws Exception {
		
		// 로그인 정보 검증
		if (loginCheck(memberDTO) != 1) {
			// 로그인 실패 시 에러 메시지와 함께 로그인 페이지로 리다이렉트
			String msg = URLEncoder.encode("아이디 또는 비밀번호가 일치하지 않읍니다.", "utf-8");
			return "redirect:/login/?msg=" + msg;
		}
		
		// 세션 생성 및 사용자 ID 저장
		HttpSession session = request.getSession();
		session.setAttribute("id", memberDTO.getId());
		
		// 아이디 저장(Remember me) 기능 처리
		if(rememberId) {
			// 7일간 유지되는 쿠키 생성
			Cookie cookie = new Cookie("id", memberDTO.getId());
			cookie.setPath("/");
			cookie.setMaxAge(60*60*24*7);
			response.addCookie(cookie);
		}else {
			// 기존 쿠키 삭제
	        Cookie idCookie = new Cookie("id", "");
	        idCookie.setPath("/");
	        idCookie.setMaxAge(0); // 쿠키 즉시 삭제
	        response.addCookie(idCookie);
	    }
		
		// 리다이렉트 URL 결정 (기본값: 메인 페이지)
		toURL = toURL == null || toURL.equals("") ? "/index" : toURL;
		
		return "redirect:" + toURL;
	}
	
	// 로그아웃 메서드
	// 1. 세션 무효화
	// 2. 쿠키 삭제
	// 3. 메인 페이지로 리다이렉트
	@GetMapping("/logout")
	public String logout(HttpSession session, HttpServletResponse response) {
		//세션 무효화
		session.invalidate();
		
		// 쿠키 삭제
		Cookie idCookie = new Cookie("id", "");
	    idCookie.setPath("/");
	    idCookie.setMaxAge(0);
	    response.addCookie(idCookie);

	    //메인 페이지로 리다이렉트
		return "redirect:/index";
	}
	
	// 회원 탈퇴 메서드
	// 1. 사용자 계정 삭제
	// 2. 세션 무효화
	// 3. 성공 메시지와 함께 메인 페이지로 이동
	@PostMapping("/remove")
	public String remove(MemberDTO memberDTO, Model model, HttpSession session) {

		System.out.println("a");
		try {
			
			String id = (String)session.getAttribute("id");
			// 사용자 정보를 기반으로 계정 삭제 처리
			int rowCnt = memberService.delete(id);
			System.out.println("a");
			// 로그인 세션 초기화
			session.invalidate();

			// 삭제 완료 메시지 및 경로를 Model에 저장
			model.addAttribute("msg", "탈퇴 처리되었읍니다.");
			model.addAttribute("url", "/index");

			if (rowCnt != 1) {
				throw new Exception("remove error");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "alertPrint";
	}
	
	// 로그인 상태 체크 메서드 (private)
	// 로그인 정보 검증
	private int loginCheck(MemberDTO memberDTO) throws Exception {
		// 로그인 상태를 확인 후 결과 반환 (성공:1, 실패:0)
		int result = memberService.login(memberDTO);
		return result;
	}
}
