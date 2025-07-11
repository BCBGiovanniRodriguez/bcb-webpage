package com.bcb.webpage.controllers.customer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginController {

    @GetMapping("/customer")
    public String login(@RequestParam String param) {
        return "";
    }
    
    
}
