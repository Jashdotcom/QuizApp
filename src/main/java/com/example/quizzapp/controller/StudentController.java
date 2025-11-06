package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.QuizRepository;
import com.example.quizzapp.service.QuizResultService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private QuizRepository quizRepository;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "student-dashboard";
    }

    @PostMapping("/join")
    public String joinQuiz(@RequestParam String code, Model model) {
        Optional<Quiz> quizOpt = quizRepository.findByJoinCode(code);
        if (quizOpt.isPresent()) {
            model.addAttribute("quiz", quizOpt.get());
            return "quiz-attempt";
        }
        model.addAttribute("error", "Invalid quiz code");
        return "student-dashboard";
    }
    @Autowired
    private QuizResultService quizResultService;

    @PostMapping("/submitQuiz")
    public String submitQuiz(
            @RequestParam Long quizId,
            @RequestParam int score,
            @RequestParam int correctAnswers,
            @RequestParam int totalQuestions,
            @RequestParam int timeTaken,
            HttpSession session,
            Model model) {

        User student = (User) session.getAttribute("loggedInUser");
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();

        quizResultService.saveResult(quiz, student, score, correctAnswers, totalQuestions, timeTaken);
        model.addAttribute("message", "Quiz submitted successfully!");
        return "redirect:/student/dashboard";
    }

    @GetMapping("/myResults")
    public String viewResults(HttpSession session, Model model) {
        User student = (User) session.getAttribute("loggedInUser");
        model.addAttribute("results", quizResultService.getResultsByStudent(student));
        return "student-results";
    }

}
