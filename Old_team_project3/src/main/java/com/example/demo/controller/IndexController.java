package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    // localhost:8080 요청을 처리하는 메서드
    @GetMapping("/")
    public String getindex(Model model) {
        model.addAttribute("headerState", "default");
        return "index"; // resources/templates/index.html을 반환
    }

    @GetMapping("/login")
    public String getLogin(Model model) {
        model.addAttribute("headerState", "default");
        return "login";
    }

    @GetMapping("/signup")
    public String getSingUp(Model model) {
        model.addAttribute("headerState", "default");
        return "signup";
    }

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "dashboard";
    }
    
}