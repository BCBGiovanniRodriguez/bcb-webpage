package com.bcb.webpage.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("homeVideoUrl", "/public/legacy/pages/home/video/HomeBCB220728.mp4");
        return "index";
    }

    @GetMapping("/inicio-de-sesion")
    public String inicioSesion(Model model) {
        return "login";
    }
}
