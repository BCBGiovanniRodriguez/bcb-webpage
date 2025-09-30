package com.bcb.webpage.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OttController {

    @GetMapping("/ott/sent")
    public String sentMethod() {
        return "login/ott-sent";
    }

    @GetMapping("/login/ott")
    public String loginOttForm(Model model, HttpServletRequest request, @RequestParam String token) {
        model.addAttribute("token", token);
        
        return "login/login-ott";
    }
    
}
