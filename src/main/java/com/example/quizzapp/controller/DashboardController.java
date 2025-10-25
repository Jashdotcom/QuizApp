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

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/student")
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

    @GetMapping("/teacher")
    public String teacherDashboard(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        if (userPrincipal == null || userPrincipal.getRole() != UserRole.TEACHER) {
            return "redirect:/auth/access-denied";
        }

        // Add user data
        model.addAttribute("user", userPrincipal.getUser());

        // Add teacher's created quizzes
        List<Quiz> myQuizzes = quizService.getQuizzesByTeacher(userPrincipal.getId());
        model.addAttribute("myQuizzes", myQuizzes);

        // Add teacher stats
        model.addAttribute("quizzesCreated", quizService.getQuizzesCreatedCount(userPrincipal.getId()));
        model.addAttribute("totalParticipants", quizService.getTotalParticipants(userPrincipal.getId()));

        return "teacher-dashboard";
    }
}