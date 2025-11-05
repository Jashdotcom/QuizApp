package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.QuizAttempt;
import com.example.quizzapp.model.User;
import com.example.quizzapp.service.QuizAttemptService;
import com.example.quizzapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.quizzapp.service.QuizService;
import com.example.quizzapp.model.UserRole; // Add this import

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;
    @Autowired
    private QuizService quizService;
    @Autowired
    private QuizAttemptService quizAttemptService;

    @GetMapping("/dashboard")
    public String studentDashboard(@RequestParam Long userId, Model model) {
        User user = userService.findByUsernameById(userId).orElse(null);
        List<QuizAttempt> attempts = quizAttemptService.getAttemptsByUser(user);
        model.addAttribute("student", user);
        model.addAttribute("attempts", attempts);
        return "student/dashboard";
    }

    @GetMapping("/join")
    public String showJoinQuizForm(@RequestParam Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "student/join_quiz";
    }

    @PostMapping("/join")
    public String joinQuiz(@RequestParam Long userId,
                           @RequestParam String joinCode,
                           Model model) {
        User user = userService.findByUsernameById(userId).orElse(null);
        Quiz quiz = quizService.getQuizByJoinCode(joinCode).orElse(null);
        if (quiz == null || !quiz.isPublished()) {
            model.addAttribute("error", "Quiz not found or not published.");
            return "student/join_quiz";
        }
        model.addAttribute("quiz", quiz);
        model.addAttribute("userId", userId);
        return "student/attempt_quiz"; // Page to show/attempt quiz questions
    }

    // After student submits quiz answers:
    @PostMapping("/attempt")
    public String attemptQuiz(@RequestParam Long userId,
                              @RequestParam Long quizId,
                              @RequestParam int score) {
        User user = userService.findByUsernameById(userId).orElse(null);
        Quiz quiz = quizService.getQuizById(quizId).orElse(null);
        quizAttemptService.studentAttemptQuiz(user, quiz, score);
        return "redirect:/student/dashboard?userId=" + userId;
    }

    @GetMapping("/results")
    public String myResults(@RequestParam Long userId, Model model) {
        User user = userService.findByUsernameById(userId).orElse(null);
        List<QuizAttempt> attempts = quizAttemptService.getAttemptsByUser(user);
        model.addAttribute("attempts", attempts);
        return "student/results";
    }

    @GetMapping("/quiz/{quizId}/leaderboard")
    public String quizLeaderboard(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizService.getQuizById(quizId).orElse(null);
        List<QuizAttempt> attempts = quizAttemptService.getAttemptsByQuiz(quiz);
        attempts.sort((a, b) -> b.getScore());
        model.addAttribute("quiz", quiz);
        model.addAttribute("attempts", attempts);
        return "student/leaderboard";
    }
}