package com.bcb.webpage.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bcb.webpage.service.PasswordResetService;

@Controller
public class LoginController {

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/recuperar-password")
    public String displayProcessForm() {

        return "login/recover-password";
    }

    @PostMapping("/recuperar-password")
    public String processForm(@RequestParam String contractNumber, Model model) {
        
        try {
            passwordResetService.generateToken(contractNumber);
            model.addAttribute("message", "Se ha enviado un mensaje al correo electrónico asociado al número de contrato proporcionado");
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage());
            model.addAttribute("error", "No se encontró el número de contrato.");
        }

        return "login/recover-password";
    }

    @GetMapping("/nuevo-password")
    public String displayResetPasswordForm(@RequestParam String token, Model model) {
        
        model.addAttribute("token", token);

        return "login/reset-password";
    }
    
    @PostMapping("/nuevo-password")
    public String processResetPasswordForm(@RequestParam String token, @RequestParam String password, Model model) {
        
        try {
            passwordResetService.resetPassword(token, password);
            model.addAttribute("mensaje", "Contraseña reestablecida exitosamente");
            
            return "public/login";
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage());

            model.addAttribute("error", e.getMessage());
            return "login/reset-password";
        }
        
    }
    
    
}
