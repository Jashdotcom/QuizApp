package com.example.quizzapp.controller;

import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.ResultRepository;
import com.example.quizzapp.repository.UserRepository;
import com.example.quizzapp.security.UserPrincipal;
import com.example.quizzapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String username = userPrincipal.getUsername();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "redirect:/auth/login";
        }

        User user = userOptional.get();
        List<QuizResult> userResults = resultRepository.findByUser(user);

        long attemptedCount = userResults.size();
        double averageScore = userResults.stream()
                .mapToInt(QuizResult::getScore)
                .average()
                .orElse(0.0);

        model.addAttribute("quizzes", quizService.getAvailableQuizzes());
        model.addAttribute("user", user);
        model.addAttribute("attemptedCount", attemptedCount);
        model.addAttribute("averageScore", Math.round(averageScore));
        model.addAttribute("totalScore", userResults.stream().mapToInt(QuizResult::getScore).sum());
        return "student-dashboard";
    }

    @GetMapping("/quizzes")
    public String viewQuizzes(Model model) {
        model.addAttribute("quizzes", quizService.getAllActiveQuizzes());
        return "student-quizzes";
    }

    @GetMapping("/results")
    public String viewMyResults(Model model, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String username = userPrincipal.getUsername();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "redirect:/auth/login";
        }

        User user = userOptional.get();
        List<QuizResult> results = resultRepository.findByUser(user);
        results.sort((a, b) -> b.getCompletedAt().compareTo(a.getCompletedAt()));

        model.addAttribute("results", results);
        model.addAttribute("user", user);

        return "student-results";
    }

    @GetMapping("/leaderboard")
    public String viewLeaderboard(Model model, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        List<QuizResult> allResults = resultRepository.findAll();
        allResults.sort((a, b) -> {
            int scoreCompare = b.getScore().compareTo(a.getScore());
            if (scoreCompare != 0) return scoreCompare;
            return a.getCompletedAt().compareTo(b.getCompletedAt());
        });

        model.addAttribute("leaderboard", allResults);
        model.addAttribute("username", userPrincipal.getUsername());

        return "student-leaderboard";
    }
}