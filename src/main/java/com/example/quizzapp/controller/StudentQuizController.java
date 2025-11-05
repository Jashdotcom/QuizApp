package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.quizzapp.service.QuizService; // Add this import

@Controller
@RequestMapping("/student")
public class StudentQuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/joinQuiz")
    public String showJoinQuizForm() {
        return "student/join_quiz";
    }

    @PostMapping("/joinQuiz")
    public String joinQuiz(@RequestParam String joinCode, Model model) {
        Quiz quiz = quizService.findQuizByCode(joinCode).orElse(null);
        if (quiz == null) {
            model.addAttribute("error", "No quiz exists with that code!");
            return "student/join_quiz";
        }
        model.addAttribute("quiz", quiz);
        // display quiz info for attempt
        return "student/attempt_quiz";
    }

    @GetMapping("/quiz/{quizId}/attempt")
    public String attemptQuiz(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizService.getPublishedQuizById(quizId).orElse(null);
        if (quiz == null) {
            model.addAttribute("error", "Quiz not found or is unpublished.");
            return "student/dashboard";
        }
        model.addAttribute("quiz", quiz);
        return "student/attempt_quiz";
    }
}