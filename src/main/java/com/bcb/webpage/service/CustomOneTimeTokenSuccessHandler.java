package com.bcb.webpage.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomOneTimeTokenSuccessHandler implements AuthenticationSuccessHandler {

    private String successRedirectUrl;

    public CustomOneTimeTokenSuccessHandler(String successRedirectUrl) {
        this.successRedirectUrl = successRedirectUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
                
        response.sendRedirect(successRedirectUrl);
    }

}
