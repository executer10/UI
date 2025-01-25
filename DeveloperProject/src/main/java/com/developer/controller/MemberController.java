package com.developer.controller;

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

import com.developer.dto.MemberDTO;
import com.developer.service.MemberService;

@Controller
public class MemberController {

	@Autowired
	MemberService memberService;
	
	@GetMapping("/login")
	public String login() {
		return "/member/login";
	}
	
	@GetMapping("/join")
	public String join() {
		return "/member/join";
	}
	
	@ResponseBody
	@PostMapping("/join/idOverlap")
	public Integer idOverlap(MemberDTO memberDTO) throws Exception{
		try {
			int result = memberService.idOverlap(memberDTO);
	        return result;
	    } 
		catch (Exception e) {
	        e.printStackTrace();
	        return -1;  // 에러 발생 시 -1 반환
	    }
	}
	
	@PostMapping("/join/register")
	public String PostJoin(MemberDTO memberDTO, Model model) throws Exception{
		System.out.println("MemberController : " + memberDTO);
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
		return "/member/alertPrint";
	}
	
	@PostMapping("login/processing")
	public String login(MemberDTO memberDTO, String toURL, boolean rememberId, HttpServletResponse response, HttpServletRequest request, Model model) throws Exception {
		
		// 로그인 정보 검증
		if (loginCheck(memberDTO) != 1) {
			model.addAttribute("msg", "아이디 혹은 비밀번호가 일치하지 않읍니다.");
			model.addAttribute("url", "/login");
			return "/member/alertPrint";
		}
		
		// 세션 생성 및 사용자 ID 저장
		HttpSession session = request.getSession();
		session.setAttribute("user_id", memberDTO.getUser_id());
		
		// 아이디 저장(Remember me) 기능 처리
		if(rememberId) {
			// 7일간 유지되는 쿠키 생성
			Cookie cookie = new Cookie("user_id", memberDTO.getUser_id());
			cookie.setPath("/");
			cookie.setMaxAge(60*60*24*7);
			response.addCookie(cookie);
		}else {
			// 기존 쿠키 삭제
	        Cookie idCookie = new Cookie("user_id", "");
	        idCookie.setPath("/");
	        idCookie.setMaxAge(0); // 쿠키 즉시 삭제
	        response.addCookie(idCookie);
	    }
		
		// 리다이렉트 URL 결정 (기본값: 메인 페이지)
		toURL = toURL == null || toURL.equals("") ? "/main" : toURL;
		
		return "redirect:" + toURL;
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session, HttpServletResponse response) {
		//세션 무효화
		session.invalidate();
		
		// 쿠키 삭제
		Cookie idCookie = new Cookie("user_id", "");
	    idCookie.setPath("/");
	    idCookie.setMaxAge(0);
	    response.addCookie(idCookie);

	    //메인 페이지로 리다이렉트
		return "redirect:/main";
	}
	
	@PostMapping("/remove")
	public String remove(MemberDTO memberDTO, Model model, HttpSession session) {

		try {
			// 사용자 정보를 기반으로 계정 삭제 처리
			int rowCnt = memberService.delete(memberDTO);

			// 로그인 세션 초기화
			session.invalidate();

			// 삭제 완료 메시지 및 경로를 Model에 저장
			model.addAttribute("msg", "탈퇴 처리되었읍니다.");
			model.addAttribute("url", "/main");

			if (rowCnt != 1) {
				throw new Exception("remove error");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/member/alertPrint";
	}
	
	private int loginCheck(MemberDTO memberDTO) throws Exception {
		// 로그인 상태를 확인 후 결과 반환 (성공:1, 실패:0)
		int result = memberService.login(memberDTO);
		return result;
	}
}
