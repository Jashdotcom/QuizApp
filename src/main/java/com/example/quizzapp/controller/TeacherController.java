package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.model.QuizStatus;
import com.example.quizzapp.repository.ResultRepository;
import com.example.quizzapp.security.UserPrincipal;
import com.example.quizzapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private ResultRepository resultRepository;

    @GetMapping("/quizzes/create")
    public String showCreateQuizForm(Model model) {
        model.addAttribute("quiz", new com.example.quizzapp.model.Quiz());
        return "quiz-form";
    }

    @GetMapping("/dashboard")
    public String teacherDashboard(Model model, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String teacherName = userPrincipal.getUsername();

        List<Quiz> allQuizzes = quizService.getAllQuizzes();
        List<Quiz> publishedQuizzes = allQuizzes.stream()
                .filter(q -> q.getStatus() == QuizStatus.PUBLISHED)
                .collect(Collectors.toList());

        long publishedCount = publishedQuizzes.size();
        long totalQuizzes = allQuizzes.size();

        model.addAttribute("quizzes", allQuizzes);
        model.addAttribute("publishedQuizzes", publishedQuizzes);
        model.addAttribute("teacherName", teacherName);
        model.addAttribute("totalQuizzes", totalQuizzes);
        model.addAttribute("publishedCount", publishedCount);

        return "teacher-dashboard";
    }

    @GetMapping("/quizzes/{quizId}/students")
    public String viewQuizStudents(@PathVariable Long quizId, Model model, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Quiz quiz = quizService.getQuizById(quizId).orElse(null);

        if (quiz == null) {
            model.addAttribute("error", "Quiz not found");
            return "redirect:/teacher/dashboard";
        }

        List<QuizResult> results = resultRepository.findByQuizId(quizId);

        model.addAttribute("quiz", quiz);
        model.addAttribute("results", results);
        model.addAttribute("teacherName", userPrincipal.getUsername());

        return "teacher-quiz-students";
    }

    @GetMapping("/quizzes/{quizId}/leaderboard")
    public String viewQuizLeaderboard(@PathVariable Long quizId, Model model, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Quiz quiz = quizService.getQuizById(quizId).orElse(null);

        if (quiz == null) {
            model.addAttribute("error", "Quiz not found");
            return "redirect:/teacher/dashboard";
        }

        List<QuizResult> results = resultRepository.findByQuizId(quizId);
        results.sort((a, b) -> {
            int scoreCompare = b.getScore().compareTo(a.getScore());
            if (scoreCompare != 0) return scoreCompare;
            return a.getCompletedAt().compareTo(b.getCompletedAt());
        });

        model.addAttribute("quiz", quiz);
        model.addAttribute("leaderboard", results);
        model.addAttribute("teacherName", userPrincipal.getUsername());

        return "teacher-quiz-leaderboard";
    }

    @GetMapping("/leaderboard")
    public String viewGlobalLeaderboard(Model model, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        List<QuizResult> allResults = resultRepository.findAll();
        allResults.sort((a, b) -> {
            int scoreCompare = b.getScore().compareTo(a.getScore());
            if (scoreCompare != 0) return scoreCompare;
            return a.getCompletedAt().compareTo(b.getCompletedAt());
        });

        model.addAttribute("leaderboard", allResults);
        model.addAttribute("teacherName", userPrincipal.getUsername());

        return "teacher-leaderboard";
    }
}