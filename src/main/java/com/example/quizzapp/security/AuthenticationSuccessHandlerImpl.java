package com.example.quizzapp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationSuccessHandlerImpl.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("Authentication success for user={}", authentication.getName());

        // Decide target based on authorities to avoid extra controller redirect
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String targetUrl = "/student/dashboard"; // default
        if (authorities != null) {
            for (GrantedAuthority a : authorities) {
                String role = a.getAuthority();
                if (role != null && role.contains("TEACHER")) {
                    targetUrl = "/teacher/dashboard";
                    break;
                }
                if (role != null && role.contains("ADMIN")) {
                    targetUrl = "/teacher/dashboard"; // admin -> teacher area for now
                    break;
                }
            }
        }

        response.sendRedirect(request.getContextPath() + targetUrl);
    }
}
