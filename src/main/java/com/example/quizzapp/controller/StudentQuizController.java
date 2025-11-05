package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentQuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/join")
    public String showJoinQuizForm(Model model) {
        return "auth/student-join-quiz";
    }

    @PostMapping("/join")
    public String joinQuiz(@RequestParam String quizCode, Model model) {
        Optional<Quiz> quizOptional = quizService.findQuizByCode(quizCode);

        if (quizOptional.isPresent()) {
            Quiz quiz = quizOptional.get();
            model.addAttribute("quiz", quiz);
            return "auth/student-quiz-details"; // Show quiz details before starting
        } else {
            model.addAttribute("error", "Invalid quiz code. Please check the code and try again.");
            return "auth/student-join-quiz";
        }
    }

    @GetMapping("/quiz/{quizId}")
    public String takeQuiz(@PathVariable Long quizId, Model model) {
        Optional<Quiz> quizOptional = quizService.getPublishedQuizById(quizId);

        if (quizOptional.isPresent()) {
            model.addAttribute("quiz", quizOptional.get());
            return "auth/student-quiz-details"; // Page where student actually takes the quiz
        } else {
            model.addAttribute("error", "Quiz not found or not available.");
            return "auth/student-join-quiz";
        }
    }
}