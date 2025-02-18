package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.MemberDTO;
import com.example.demo.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {

    @Autowired
    MemberService memberService;

    // ---------------------------회원 가입------------------------------------//
    @GetMapping("/join")
    public String getJoin() {
        return "/member/join";
    }

    @ResponseBody
    @PostMapping("/join/idOverlap")
    public Integer idOverlap(MemberDTO memberDTO) throws Exception {
        try {
            int result = memberService.idOverlap(memberDTO);
            System.out.println("idOverlap : " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @PostMapping("/join/register")
    public String postJoin(MemberDTO memberDTO, Model model) throws Exception {
        int result = idOverlap(memberDTO);

        // 아이디 중복시 회원가입 페이지로 이동(다시 입력)
        if (result != 0) {
            return "redirect:/join";
        }

        // 회원 정보 등록(회원가입 완료 시 DB에 암호화된 비밀번호 저장)
        memberService.register(memberDTO);

        // 성공 메세지와 로그인 페이지 URL 설정
        model.addAttribute("msg", "회원 가입이 완료되었습니다.");
        model.addAttribute("url", "/login");

        return "alertPrint";
    }

    // ---------------------------로그인---------------------------------------//
    @GetMapping("/login")
    public String getLogin() {
        return "/member/login";
    }

    @PostMapping("login/processing")
    public String postLogin(MemberDTO memberDTO, String toURL, boolean rememberId, HttpServletResponse response,
            HttpServletRequest request, Model model) throws Exception {

        // 로그인 정보 검증
        int loginResult = memberService.login(memberDTO);
        if (loginResult != 1) {
            model.addAttribute("msg", "아이디 혹은 비밀번호가 일치하지 않습니다.");
            model.addAttribute("url", "/login");
            return "alertPrint";
        }

        // 로그인 성공 시 사용자 정보를 새로 조회
        MemberDTO userInfo = memberService.getUserById(memberDTO.getUserId());

        // 세션 생성 및 사용자 ID 저장
        HttpSession session = request.getSession();
        session.setAttribute("userId", userInfo.getUserId());

        // 아이디 저장 쿠키 처리
        if (rememberId) {
            Cookie cookie = new Cookie("userId", memberDTO.getUserId());
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7); // 7일
            response.addCookie(cookie);
        } else {
            Cookie idCookie = new Cookie("userId", "");
            idCookie.setPath("/");
            idCookie.setMaxAge(0); // 7일
            response.addCookie(idCookie);
        }

        toURL = (toURL == null || toURL.equals("")) ? "/main" : toURL;
        return "redirect:" + toURL;
    }

    // -----------------------------로그 아웃------------------------------------//
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        // 세션 무효화 및 쿠키 삭제
        session.invalidate();
        Cookie idCookie = new Cookie("userId", "");
        idCookie.setPath("/");
        idCookie.setMaxAge(0);
        response.addCookie(idCookie);
        return "redirect:/main";
    }

    // -----------------------------회원 수정------------------------------//
    @GetMapping("/memberModify")
    public String getMemberModify(HttpSession session, Model model) {
        // 세션에서 UserId 가져옴
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/login");
            return "alertPrint";
        }

        try {
            // Service를 통해 DB 조회
            MemberDTO storedUser = memberService.getUserById(userId);
            // 모델에 실어서 Thymeleaf로 넘김
            model.addAttribute("user", storedUser);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", "회원 정보를 가져오는 중 오류가 발생했습니다.");
            model.addAttribute("url", "/index");
            return "alertPrint";
        }
        return "/member/memberModify";
    }

    @PostMapping("/memberModify")
    public String getMemberUpdate(MemberDTO memberDTO,
            @RequestParam("userNewPw") String newPassword,
            HttpSession session,
            Model model) throws Exception {

        // 세션 체크
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !userId.equals(memberDTO.getUserId())) {
            model.addAttribute("msg", "잘못된 접근입니다.");
            model.addAttribute("url", "/login");
            return "alertPrint";
        }

        int result = memberService.memberUpdate(memberDTO, newPassword);

        if (result != 1) {
            model.addAttribute("msg", "현재 비밀번호가 일치하지 않습니다.");
            model.addAttribute("url", "/memberModify");
            return "/alertPrint";
        }

        model.addAttribute("msg", "회원정보가 수정되었습니다.");
        model.addAttribute("url", "/managementPage");
        return "/alertPrint";
    }

}
