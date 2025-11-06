package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.QuizRepository;
import com.example.quizzapp.repository.ResultRepository;
import com.example.quizzapp.repository.UserRepository;
import com.example.quizzapp.security.UserPrincipal;
import com.example.quizzapp.service.QuizResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizResultService quizResultService;

    @Autowired
    private ResultRepository resultRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User student = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get available published quizzes
        List<Quiz> publishedQuizzes = quizRepository.findByPublishedTrue();
        
        // Get student's stats
        List<QuizResult> results = quizResultService.getResultsByStudent(student);
        int attemptedCount = results.size();
        int totalScore = results.stream().mapToInt(QuizResult::getScore).sum();
        int averageScore = attemptedCount > 0 ? totalScore / attemptedCount : 0;

        model.addAttribute("username", userPrincipal.getUsername());
        model.addAttribute("quizzes", publishedQuizzes);
        model.addAttribute("attemptedCount", attemptedCount);
        model.addAttribute("totalScore", totalScore);
        model.addAttribute("averageScore", averageScore);
        return "student-dashboard";
    }

    @GetMapping("/join")
    public String showJoinQuizPage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        model.addAttribute("username", userPrincipal.getUsername());
        return "student-join-quiz";
    }

    @PostMapping("/join")
    public String joinQuiz(@RequestParam String quizCode, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Optional<Quiz> quizOpt = quizRepository.findByJoinCodeAndPublishedTrue(quizCode);
        if (quizOpt.isPresent()) {
            model.addAttribute("quiz", quizOpt.get());
            return "quiz-attempt";
        }
        model.addAttribute("error", "Invalid quiz code or quiz not published");
        model.addAttribute("username", userPrincipal.getUsername());
        return "student-join-quiz";
    }

    @PostMapping("/submitQuiz")
    public String submitQuiz(
            @RequestParam Long quizId,
            @RequestParam int score,
            @RequestParam int correctAnswers,
            @RequestParam int totalQuestions,
            @RequestParam int timeTaken,
            Authentication authentication,
            Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User student = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        // Add empty quizzes list for now (to fix the error)
        model.addAttribute("quizzes", List.of());

        Quiz quiz = quizRepository.findById(quizId).orElseThrow();

        quizResultService.saveResult(quiz, student, score, correctAnswers, totalQuestions, timeTaken);
        model.addAttribute("message", "Quiz submitted successfully!");
        return "redirect:/student/dashboard";
    }

    @GetMapping("/quizzes")
    public String showAvailableQuizzes(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<Quiz> publishedQuizzes = quizRepository.findByPublishedTrue();

        model.addAttribute("username", userPrincipal.getUsername());
        model.addAttribute("quizzes", publishedQuizzes);
        return "student/quiz-attempt";
    }

    @GetMapping("/quizzes/{id}")
    public String attemptQuiz(@PathVariable Long id, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        Optional<Quiz> quizOpt = quizRepository.findByIdAndPublishedTrue(id);
        if (quizOpt.isPresent()) {
            model.addAttribute("quiz", quizOpt.get());
            return "quiz-attempt";
        }
        return "redirect:/student/quizzes";
    }

    @GetMapping("/results")
    public String viewResults(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User student = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("username", userPrincipal.getUsername());
        model.addAttribute("results", quizResultService.getResultsByStudent(student));
        return "student-results";
    }

    @GetMapping("/myResults")
    public String viewResultsLegacy(Authentication authentication, Model model) {
        // Redirect to the new /results endpoint for backward compatibility
        return "redirect:/student/results";
    }

    @GetMapping("/leaderboard")
    public String showLeaderboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        // Get all quiz results ordered by score (descending) using database query
        List<QuizResult> allResults = resultRepository.findAllByOrderByScoreDesc();

        model.addAttribute("username", userPrincipal.getUsername());
        model.addAttribute("leaderboard", allResults);
        return "student-results";
    }

}