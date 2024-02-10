package com.UI.controller;

/*UserController.java*/
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

import com.UI.domain.UserDTO;
import com.UI.service.UserService;

@Controller
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("/") // 접속시 기본 페이지를 반환 // 로그인 페이지 메서드
	public String index() {
		return "index";
	}

	@GetMapping("/join") // // 회원가입 페이지로 이동
	public String join() {
		return "join";
	}

	@PostMapping("/join/success") // 회원가입 처리 메서드
	public String success(UserDTO userDTO, Model model) throws Exception {

		// 아이디 중복 체크를 위한 메서드 호출
		int result = idOverlap(userDTO);

		// 아이디가 중복될 경우 회원가입 페이지로 다시 이동
		if (result != 0) {
			return "redirect:/join";
		}

		// 회원가입 처리 메서드 호출
		userService.register(userDTO);

		// 회원가입 완료 메시지 및 리다이렉션 경로를 Model에 저장
		model.addAttribute("msg", "회원 가입이 완료되었읍니다.");
		model.addAttribute("url", "/UI");

		// 회원가입 완료 후 alertPrint 페이지로 이동하여 사용자에게 메시지를 보여줌
		return "alertPrint";

	}

	// 아이디 중복 체크 메서드
	@ResponseBody
	@PostMapping("/join/idOverlap")
	public int idOverlap(UserDTO userDTO) throws Exception {

		// 아이디 중복 체크 결과를 반환하는 메서드를 호출하여 결과를 저장
		int result = userService.idOverlap(userDTO);

		// 중복 체크 결과 반환
		return result;
	}

	// 로그인 처리 메서드
	@PostMapping("/")
	public String login(UserDTO userDTO, String toURL, boolean rememberId, HttpServletResponse response,
			HttpServletRequest request) throws Exception {

		// 사용자가 입력한 정보를 기반으로 로그인 체크를 진행하고 결과를 저장
		if (loginCheck(userDTO) != 1) {
			// 로그인 실패 시 에러 메시지를 인코딩하여 URL에 추가하여 사용자에게 보여줌
			String msg = URLEncoder.encode("아이디 또는 비밀번호가 일치하지 않읍니다.", "utf-8");
			return "redirect:/?msg=" + msg;
		}

		// id와 pwd가 일치하면 홈으로 이동
		// 로그인을 성공했을 경우, HttpSession 객체를 얻어옴
		HttpSession session = request.getSession();

		// 세션에 로그인한 사용자 아이디를 속성으로 저장
		session.setAttribute("id", userDTO.getId());

		// 아이디 기억하기 기능
		if (rememberId) {
			// 쿠키 생성 및 아이디 값 저장
			Cookie cookie = new Cookie("id", userDTO.getId());

			// 생성된 쿠키를 응답에 추가
			response.addCookie(cookie);
		} else {
			// 기억하기 취소 시, 쿠키 삭제
			Cookie cookie = new Cookie("id", userDTO.getId());
			cookie.setMaxAge(0);// 쿠키 삭제

			// 삭제된 쿠키를 응답에 추가하여 저장
			response.addCookie(cookie);
		}

		// 사용자가 로그인 성공 후 이동할 페이지를 지정하거나 기본 경로로 사용
		toURL = toURL == null || toURL.equals("") ? "/" : toURL;

		// 지정된 페이지로 리다이렉트
		return "redirect:" + toURL;
	}

	// 로그아웃 메서드
	@GetMapping("/logout")
	public String logout(HttpSession session) {

		// 세션에 저장된 아이디 정보를우기 위해 세션 초기화
		session.invalidate();

		// 로그아웃 후 메인 페이지로 이동
		return "redirect:/";
	}

	// 계정 삭제 메서드
	@PostMapping("/remove")
	public String remove(UserDTO userDTO, Model model, HttpSession session) {

		try {
			// 사용자 정보를 기반으로 계정 삭제 처리
			int rowCnt = userService.delete(userDTO);

			// 로그인 세션 초기화
			session.invalidate();

			// 삭제 완료 메시지 및 경로를 Model에 저장
			model.addAttribute("msg", "탈퇴 처리되었읍니다.");
			model.addAttribute("url", "/UI");

			if (rowCnt != 1) {
				throw new Exception("remove error");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "alertPrint";
	}

	// 로그인 상태 체크 메서드
	private int loginCheck(UserDTO usetDTO) throws Exception {

		// 로그인 상태를 확인 후 결과 반환
		int result = userService.login(usetDTO);
		return result;
	}
	
	/*-----------------------------------------------------------*/
	
	@GetMapping("/boardList/languageSelection") // 접속시 기본 페이지를 반환 // 메인 페이지 메서드
	public String languageSelection() {
		return "languageSelection";
	}
}
