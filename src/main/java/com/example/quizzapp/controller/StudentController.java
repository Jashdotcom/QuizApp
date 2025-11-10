// File: src/main/java/com/example/quizzapp/controller/StudentController.java
package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.service.QuizResultService;
import com.example.quizzapp.service.QuizService;
import com.example.quizzapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizResultService resultService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Principal principal) {
        String username = principal.getName();
        User student = userService.findByUsername(username);

        List<Quiz> availableQuizzes = quizService.getAllQuizzes();
        Double averageScore = resultService.getAverageScoreByStudent(student);

        model.addAttribute("student", student);
        model.addAttribute("quizzes", availableQuizzes);
        model.addAttribute("averageScore", averageScore != null ? averageScore : 0.0);

        return "student/dashboard";
    }

    @GetMapping("/join-quiz")
    public String showJoinQuizPage() {
        return "student/join-quiz";
    }

    @PostMapping("/join-quiz")
    public String joinQuiz(@RequestParam String quizCode, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            Quiz quiz = quizService.getQuizByCode(quizCode);
            if (quiz != null) {
                return "redirect:/student/attempt-quiz/" + quiz.getId();
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid quiz code");
                return "redirect:/student/join-quiz";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid quiz code");
            return "redirect:/student/join-quiz";
        }
    }

    @GetMapping("/attempt-quiz")
    public String showAvailableQuizzes(Model model) {
        List<Quiz> availableQuizzes = quizService.getAllQuizzes();
        model.addAttribute("quizzes", availableQuizzes);
        return "student/attempt-quiz";
    }

    @GetMapping("/my-results")
    public String showMyResults(Model model, Principal principal) {
        String username = principal.getName();
        User student = userService.findByUsername(username);
        List<QuizResult> results = resultService.getResultsByStudent(student);
        model.addAttribute("results", results);
        return "student/my-results";
    }

    @GetMapping("/leaderboard")
    public String showLeaderboard(Model model) {
        List<Object[]> leaderboard = resultService.getLeaderboard();
        model.addAttribute("leaderboard", leaderboard);
        return "student/leaderboard";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
}
