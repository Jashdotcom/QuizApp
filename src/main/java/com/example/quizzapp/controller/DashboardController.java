package com.example.quizzapp.controller;

import com.example.quizzapp.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String role = userPrincipal.getRole().name();

        if ("TEACHER".equals(role)) {
            return "redirect:/teacher/dashboard";
        } else if ("STUDENT".equals(role)) {
            return "redirect:/student/dashboard";
        }

        return "redirect:/auth/login";
    }
}