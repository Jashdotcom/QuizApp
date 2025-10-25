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

    @PostMapping("/quizzes/{quizId}/publish")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> publishQuiz(
            @PathVariable Long quizId,
            Authentication authentication) {

        String username = authentication.getName();
        User teacher = userService.getUserByUsername(username);

        Map<String, Object> response = new HashMap<>();

        if (teacher != null && quizService.isQuizOwner(quizId, teacher.getId())) {
            boolean success = quizService.publishQuiz(quizId, teacher.getId());
            if (success) {
                response.put("success", true);
                response.put("message", "Quiz published successfully!");
            } else {
                response.put("success", false);
                response.put("message", "Cannot publish quiz. Make sure it has at least one question.");
            }
        } else {
            response.put("success", false);
            response.put("message", "You don't have permission to publish this quiz.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/quizzes/{quizId}/unpublish")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unpublishQuiz(
            @PathVariable Long quizId,
            Authentication authentication) {

        String username = authentication.getName();
        User teacher = userService.getUserByUsername(username);

        Map<String, Object> response = new HashMap<>();

        if (teacher != null && quizService.isQuizOwner(quizId, teacher.getId())) {
            boolean success = quizService.unpublishQuiz(quizId, teacher.getId());
            if (success) {
                response.put("success", true);
                response.put("message", "Quiz unpublished successfully!");
            } else {
                response.put("success", false);
                response.put("message", "Failed to unpublish quiz.");
            }
        } else {
            response.put("success", false);
            response.put("message", "You don't have permission to unpublish this quiz.");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/quizzes/published")
    @ResponseBody
    public ResponseEntity<List<QuizDTO>> getPublishedQuizzes(Authentication authentication) {
        String username = authentication.getName();
        User teacher = userService.getUserByUsername(username);

        if (teacher == null) {
            return ResponseEntity.ok(List.of());
        }

        List<QuizDTO> publishedQuizzes = quizService.getPublishedQuizzesByTeacher(teacher.getId());
        return ResponseEntity.ok(publishedQuizzes);
    }

    @GetMapping("/quizzes/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getQuizStats(Authentication authentication) {
        String username = authentication.getName();
        User teacher = userService.getUserByUsername(username);

        Map<String, Object> stats = new HashMap<>();

        if (teacher != null) {
            List<QuizDTO> allQuizzes = quizService.getQuizzesByTeacherAsDTO(teacher.getId());
            List<QuizDTO> publishedQuizzes = quizService.getPublishedQuizzesByTeacher(teacher.getId());

            stats.put("totalQuizzes", allQuizzes.size());
            stats.put("publishedCount", publishedQuizzes.size());
            stats.put("unpublishedCount", allQuizzes.size() - publishedQuizzes.size());
        } else {
            stats.put("totalQuizzes", 0);
            stats.put("publishedCount", 0);
            stats.put("unpublishedCount", 0);
        }

        return ResponseEntity.ok(stats);
    }
}