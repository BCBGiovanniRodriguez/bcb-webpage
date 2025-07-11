package com.bcb.webpage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {
    @GetMapping("/error")
    public String error(Model model) {
        model.addAttribute("mensaje", "¡Hola, mundo!");
        model.addAttribute("homeVideoUrl", "/videos/33818/4710/HomeBCB220728.mp4");


        return "error";
    }
}
