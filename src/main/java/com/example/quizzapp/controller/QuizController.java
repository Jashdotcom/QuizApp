package com.example.quizzapp.controller;

import com.example.quizzapp.model.Question;
import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/{quizId}")
    public String viewQuiz(@PathVariable Long quizId, Model model) {
        Optional<Quiz> quiz = quizService.getQuizById(quizId);
        model.addAttribute("quiz", quiz);
        return "quiz-view";
    }

    @PostMapping("/publish/{quizId}")
    public String publishQuiz(@PathVariable Long quizId) {
        quizService.publishQuiz(quizId);
        return "redirect:/teacher/dashboard";
    }

    @PostMapping("/unpublish/{quizId}")
    public String unpublishQuiz(@PathVariable Long quizId) {
        quizService.unpublishQuiz(quizId);
        return "redirect:/teacher/dashboard";
    }

    @PostMapping("/{quizId}/add-question")
    public String addQuestion(@PathVariable Long quizId,
                              @RequestParam String questionText,
                              @RequestParam List<String> options,
                              @RequestParam int correctOptionIndex) {
        quizService.addQuestionToQuiz(quizId, questionText, options, correctOptionIndex);
        return "redirect:/quiz/" + quizId;
    }

    @PostMapping("/delete/{quizId}")
    public String deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return "redirect:/teacher/dashboard";
    }
    // GET method for showing the join quiz form
    @GetMapping("/join-quiz")
    public String showJoinQuizForm(Model model) {
        return "join-quiz"; // This should be your join quiz form page
    }
    @PostMapping("/join-quiz")
    public String joinQuiz(@RequestParam String joinCode,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        // Your join quiz logic here
        return "redirect:/quiz/";
    }
}
