package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.UserRole;
import com.example.quizzapp.security.UserPrincipal;
import com.example.quizzapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/dashboard")
    public String studentDashboard(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        if (userPrincipal == null || userPrincipal.getRole() != UserRole.STUDENT) {
            return "redirect:/auth/access-denied";
        }

        // Add user data
        model.addAttribute("user", userPrincipal.getUser());

        // Add available quizzes
        List<Quiz> availableQuizzes = quizService.getAvailableQuizzes();
        model.addAttribute("quizzes", availableQuizzes);

        // Add user stats
        model.addAttribute("quizzesAttempted", quizService.getQuizzesAttemptedCount(userPrincipal.getId()));
        model.addAttribute("averageScore", quizService.getAverageScore(userPrincipal.getId()));
        model.addAttribute("totalScore", quizService.getTotalScore(userPrincipal.getId()));

        return "student-dashboard";
    }

    @GetMapping("/quizzes")
    public String viewAvailableQuizzes(Model model) {
        List<Quiz> availableQuizzes = quizService.getAllActiveQuizzes();
        model.addAttribute("quizzes", availableQuizzes);
        return "student-quizzes";
    }

    @GetMapping("/quizzes/{quizId}")
    public String viewQuiz(@PathVariable Long quizId, Model model) {
        // Get the quiz and check if it's published
        Quiz quiz = quizService.getQuizById(quizId).orElse(null);

        if (quiz == null || !quiz.isPublished()) {
            return "redirect:/student/quizzes?error=quiz_not_found";
        }

        model.addAttribute("quiz", quiz);
        return "student-quiz-attempt";
    }

    @GetMapping("/results")
    public String viewResults(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        if (userPrincipal == null || userPrincipal.getRole() != UserRole.STUDENT) {
            return "redirect:/auth/access-denied";
        }

        // Add user's quiz results/history
        // TODO: Implement results service
        model.addAttribute("user", userPrincipal.getUser());
        return "student-results";
    }

    @GetMapping("/leaderboard")
    public String viewLeaderboard(Model model) {
        // TODO: Implement leaderboard service
        return "student-leaderboard";
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        if (userPrincipal == null || userPrincipal.getRole() != UserRole.STUDENT) {
            return "redirect:/auth/access-denied";
        }

        model.addAttribute("user", userPrincipal.getUser());
        return "student-profile";
    }
}