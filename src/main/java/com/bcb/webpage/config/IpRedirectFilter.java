package com.bcb.webpage.config;

import java.io.IOException;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class IpRedirectFilter extends OncePerRequestFilter {

    @Value("${app.allowed.domain}")
    private String allowedDomain;

    // Patrón para detectar direcciones IP (IPv4)
    private static final Pattern IP_PATTERN = Pattern.compile(
        "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(?::[0-9]+)?$"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
            throws ServletException, IOException {

        if (!allowedDomain.equals("localhost")) {
            
            String host = request.getHeader("Host");
            
            if (host != null && isIpAddress(host)) {
                // Construir URL de redirección con el dominio permitido
                String scheme = request.getScheme();
                String requestUri = request.getRequestURI();
                String queryString = request.getQueryString();
                
                StringBuilder redirectUrl = new StringBuilder();
                redirectUrl.append(scheme).append("://").append(allowedDomain);
                
                // Agregar puerto si no es el estándar
                if (("http".equals(scheme) && request.getServerPort() != 80) ||
                    ("https".equals(scheme) && request.getServerPort() != 443)) {
                    redirectUrl.append(":").append(request.getServerPort());
                }
                
                redirectUrl.append(requestUri);
                
                if (queryString != null && !queryString.isEmpty()) {
                    redirectUrl.append("?").append(queryString);
                }
                
                // Redirección permanente 301
                response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                response.setHeader("Location", redirectUrl.toString());
                return;
            }
            
            filterChain.doFilter(request, response);
        }
    }
    
    private boolean isIpAddress(String host) {
        // Remover puerto si existe
        String hostWithoutPort = host.split(":")[0];
        return IP_PATTERN.matcher(host).matches() || 
               IP_PATTERN.matcher(hostWithoutPort).matches();
    }
}
