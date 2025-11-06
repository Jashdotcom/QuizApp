package com.example.quizzapp.controller;

import com.example.quizzapp.model.User;
import com.example.quizzapp.model.UserRole;

import com.example.quizzapp.service.UserService;
import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Optional<User> userOpt = userService.authenticate(username, password);
        if (userOpt.isPresent()) {
            session.setAttribute("loggedInUser", userOpt.get());
            if (userOpt.get().getRole() == UserRole.TEACHER)
                return "redirect:/teacher/dashboard";
            else
                return "redirect:/student/dashboard";
        }
        model.addAttribute("error", "Invalid credentials");
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        userService.registerUser(user);
        model.addAttribute("success", "Account created! Please log in.");
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
    @GetMapping("/postLogin")
    public String postLoginRedirect(Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        if (role.equals("TEACHER")) {
            return "redirect:/teacher/dashboard";
        } else {
            return "redirect:/student/dashboard";
        }
    }


}
