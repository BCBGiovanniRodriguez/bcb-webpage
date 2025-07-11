package com.bcb.webpage.controllers.management;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller
@RequestMapping("/management")
public class DashboardController {

    @GetMapping("/login")
    public String login(Model model) {
        return "management/login";
    }

    @RequestMapping(path="/validate", method=RequestMethod.POST)
    public String requestMethodName(@RequestParam String param) {
        // obtener usuario, validar y enviar email con código

        return new String();
    }
    
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    public String validate(Model model) {


        return "management/validate";
    }

    @GetMapping("/")
    public String index(Model model) {
        return "management/index";
    }

    @GetMapping("/dashboard")
    public String home(Model model) {
        //model.addAttribute("mensaje", "¡Hola, mundo!");
        return "management/dashboard";
    }
}
