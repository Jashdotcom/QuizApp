package com.example.quizzapp.controller;

import com.example.quizzapp.dto.QuizDTO;
import com.example.quizzapp.model.User;
import com.example.quizzapp.model.UserRole;
import com.example.quizzapp.security.UserPrincipal;
import com.example.quizzapp.service.QuizService;
import com.example.quizzapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String teacherDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User teacher = userService.getUserByUsername(username);

        if (teacher == null) {
            return "redirect:/login";
        }

        List<QuizDTO> allQuizzes = quizService.getQuizzesByTeacherAsDTO(teacher.getId());
        List<QuizDTO> publishedQuizzes = quizService.getPublishedQuizzesByTeacher(teacher.getId());

        model.addAttribute("teacherName", username);
        model.addAttribute("quizzes", allQuizzes);
        model.addAttribute("publishedQuizzes", publishedQuizzes);
        model.addAttribute("totalQuizzes", allQuizzes.size());
        model.addAttribute("publishedCount", publishedQuizzes.size());

        return "teacher-dashboard";
    }
}