package com.example.quizzapp.controller;

import com.example.quizzapp.dto.SignupRequest;
import com.example.quizzapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Login page
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "auth/login";
    }

    // Remove the POST mapping - Spring Security handles login processing
    // @PostMapping("/login") is handled by Spring Security formLogin

    // Registration page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("signupRequest")) {
            model.addAttribute("signupRequest", new SignupRequest());
        }
        return "auth/register";
    }

    // Process registration
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute SignupRequest signupRequest,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.signupRequest", result);
            redirectAttributes.addFlashAttribute("signupRequest", signupRequest);
            return "redirect:/auth/register";
        }

        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            redirectAttributes.addFlashAttribute("signupRequest", signupRequest);
            return "redirect:/auth/register";
        }

        try {
            userService.registerUser(signupRequest);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("signupRequest", signupRequest);
            return "redirect:/auth/register";
        }
    }

    // Forgot password page
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    // Process forgot password
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email,
                                        RedirectAttributes redirectAttributes) {
        try {
            userService.sendPasswordResetEmail(email);
            redirectAttributes.addFlashAttribute("success", "Password reset instructions sent to your email");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error sending reset email: " + e.getMessage());
        }
        return "redirect:/auth/forgot-password";
    }

    // Reset password page
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        if (userService.validateResetToken(token)) {
            model.addAttribute("token", token);
            return "auth/reset-password";
        } else {
            model.addAttribute("error", "Invalid or expired reset token");
            return "auth/reset-password-error";
        }
    }

    // Process password reset
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String password,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/auth/reset-password?token=" + token;
        }

        if (userService.resetPassword(token, password)) {
            redirectAttributes.addFlashAttribute("success", "Password reset successfully! Please login.");
            return "redirect:/auth/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired reset token");
            return "redirect:/auth/forgot-password";
        }
    }

    // Logout success page
    @GetMapping("/logout-success")
    public String showLogoutSuccess() {
        return "auth/logout-success";
    }

    // Access denied page
    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "auth/access-denied";
    }
}