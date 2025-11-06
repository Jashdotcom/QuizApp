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

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private QuizRepository quizRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        List<Quiz> quizzes = quizRepository.findByCreatedBy(user);
        model.addAttribute("quizzes", quizzes);
        return "teacher-dashboard";
    }

    @GetMapping("/create")
    public String createQuizPage() {
        return "create-quiz";
    }

    @PostMapping("/create")
    public String createQuiz(@RequestParam String title, @RequestParam String description, HttpSession session) {
        User teacher = (User) session.getAttribute("loggedInUser");
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setJoinCode(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        quiz.setCreatedBy(teacher);
        quizRepository.save(quiz);
        return "redirect:/teacher/dashboard";
    }

    @Autowired
    private QuizResultService quizResultService;

    @GetMapping("/leaderboard/{quizId}")
    public String viewLeaderboard(@PathVariable Long quizId, Model model) {
        model.addAttribute("leaderboard", quizResultService.getLeaderboard(quizId));
        return "leaderboard";
    }

}
