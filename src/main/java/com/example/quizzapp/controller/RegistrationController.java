package com.example.quizzapp.controller;

import com.example.quizzapp.model.User;
import com.example.quizzapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.quizzapp.model.UserRole;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String role,
                               RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Registration attempt: " + username + ", " + email + ", " + role);

            // Check if username exists
            if (userService.existsByUsername(username)) {
                redirectAttributes.addFlashAttribute("error", "Username '" + username + "' is already taken!");
                redirectAttributes.addFlashAttribute("username", username);
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/register";
            }

            // Check if email exists
            if (userService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "Email '" + email + "' is already in use!");
                redirectAttributes.addFlashAttribute("username", username);
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/register";
            }

            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long!");
                redirectAttributes.addFlashAttribute("username", username);
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/register";
            }

            // Convert string to UserRole enum
            UserRole userRole;
            try {
                userRole = UserRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", "Invalid role selected!");
                redirectAttributes.addFlashAttribute("username", username);
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/register";
            }

            // Create new user (UserRole not String)
            User user = new User(username, email, passwordEncoder.encode(password), userRole);
            User savedUser = userService.save(user);

            System.out.println("User registered successfully: " + savedUser.getId());

            redirectAttributes.addFlashAttribute("success", "Registration successful! You can now login with your credentials.");
            return "redirect:/auth/login";

        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/register";
        }
    }
}