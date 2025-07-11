package com.bcb.webpage.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExampleController {
    @GetMapping("/example")
    public String error(Model model) {
        model.addAttribute("mensaje", "¡Hola, mundo!");
        model.addAttribute("homeVideoUrl", "/videos/33818/4710/HomeBCB220728.mp4");


        return "example";
    }
}
