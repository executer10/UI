package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WorkController {

    @GetMapping("/car")
    public String getCar() {
        return "/work/car";
    }

    @GetMapping("/carBody")
    public String getCarBody() {
        return "/work/carBody";
    }

}
