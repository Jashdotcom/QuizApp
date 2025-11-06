package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.QuizRepository;
import com.example.quizzapp.repository.UserRepository;
import com.example.quizzapp.security.UserPrincipal;
import com.example.quizzapp.service.QuizResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizResultService quizResultService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        // Get the current teacher user
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User teacher = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Quiz> quizzes = quizRepository.findByCreatedBy(teacher);
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("username", teacher.getUsername());
        return "teacher-dashboard";
    }

    @GetMapping("/create")
    public String createQuizPage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        return "create-quiz";
    }

    @PostMapping("/create")
    public String createQuiz(@RequestParam String title,
                             @RequestParam String description,
                             Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        // Get the current teacher user
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User teacher = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setJoinCode(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        quiz.setCreatedBy(teacher);
        quizRepository.save(quiz);

        return "redirect:/teacher/dashboard";
    }

    @GetMapping("/leaderboard/{quizId}")
    public String viewLeaderboard(@PathVariable Long quizId, Model model) {
        model.addAttribute("leaderboard", quizResultService.getLeaderboard(quizId));
        return "leaderboard";
    }
}