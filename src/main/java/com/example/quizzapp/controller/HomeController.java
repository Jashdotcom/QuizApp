package com.example.quizzapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // REMOVE this method - it's causing conflict
    // @GetMapping("/register")
    // public String register() {
    //     return "register";
    // }
}