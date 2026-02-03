package com.bcb.webpage.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HttpsRedirectFilter implements Filter {

    @Value("${app.allowed.domain}")
    private String allowedDomain;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!allowedDomain.equals("localhost")) {
            if (!httpRequest.isSecure()) {
                String httpsUrl = "https://" + httpRequest.getServerName() + httpRequest.getRequestURI();
    
                if (httpRequest.getQueryString() != null) {
                    httpsUrl += "?" + httpRequest.getQueryString();
                }
    
                httpResponse.sendRedirect(httpsUrl);
                return;
            }
    
            chain.doFilter(request, response);
        }

    }

}
