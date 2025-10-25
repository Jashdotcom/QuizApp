package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import com.example.quizzapp.model.UserRole;
import com.example.quizzapp.security.UserPrincipal;
import com.example.quizzapp.service.QuizService;
import com.example.quizzapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    @GetMapping("/student")
    public String studentDashboard(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        if (userPrincipal == null || userPrincipal.getRole() != UserRole.STUDENT) {
            return "redirect:/auth/access-denied";
        }

        model.addAttribute("user", userPrincipal.getUser());
        List<Quiz> availableQuizzes = quizService.getAvailableQuizzes();
        model.addAttribute("quizzes", availableQuizzes);
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

        model.addAttribute("user", userPrincipal.getUser());
        List<Quiz> myQuizzes = quizService.getQuizzesByTeacher(userPrincipal.getId());
        model.addAttribute("myQuizzes", myQuizzes);
        model.addAttribute("quizzesCreated", quizService.getQuizzesCreatedCount(userPrincipal.getId()));
        model.addAttribute("totalParticipants", quizService.getTotalParticipants(userPrincipal.getId()));

        return "teacher-dashboard";
    }

    // Quiz Management Methods
    @GetMapping("/teacher/quizzes/create")
    public String showCreateQuizForm(Model model, Authentication authentication) {
        model.addAttribute("quiz", new Quiz());
        return "teacher/create-quiz";
    }

    @PostMapping("/teacher/quizzes/create")
    public String createQuiz(@ModelAttribute Quiz quiz, Authentication authentication) {
        String username = authentication.getName();
        User teacher = userService.getUserByUsername(username);
        quiz.setTeacher(teacher);
        Quiz savedQuiz = quizService.saveQuiz(quiz);
        return "redirect:/teacher/quizzes/" + savedQuiz.getId() + "/edit";
    }

    @GetMapping("/teacher/quizzes/{quizId}/edit")
    public String editQuiz(@PathVariable Long quizId, Model model, Authentication authentication) {
        String username = authentication.getName();
        User teacher = userService.getUserByUsername(username);
        Quiz quiz = quizService.getQuizById(quizId).orElse(null);

        if (quiz == null || !quiz.getTeacher().getId().equals(teacher.getId())) {
            return "redirect:/teacher/dashboard?error=access_denied";
        }

        model.addAttribute("quiz", quiz);
        return "teacher/edit-quiz";
    }

    @PostMapping("/teacher/quizzes/{quizId}/update")
    public String updateQuiz(@PathVariable Long quizId, @ModelAttribute Quiz updatedQuiz, Authentication authentication) {
        String username = authentication.getName();
        User teacher = userService.getUserByUsername(username);
        Quiz existingQuiz = quizService.getQuizById(quizId).orElse(null);

        if (existingQuiz == null || !existingQuiz.getTeacher().getId().equals(teacher.getId())) {
            return "redirect:/teacher/dashboard?error=access_denied";
        }

        existingQuiz.setTitle(updatedQuiz.getTitle());
        existingQuiz.setDescription(updatedQuiz.getDescription());
        quizService.saveQuiz(existingQuiz);

        return "redirect:/teacher/quizzes/" + quizId + "/edit?success=updated";
    }

    @PostMapping("/teacher/quizzes/{quizId}/publish")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> publishQuiz(@PathVariable Long quizId, Authentication authentication) {
        String username = authentication.getName();
        User teacher = userService.getUserByUsername(username);

        Map<String, Object> response = new HashMap<>();

        if (teacher != null) {
            boolean success = quizService.publishQuiz(quizId, teacher.getId());
            if (success) {
                response.put("success", true);
                response.put("message", "Quiz published successfully! Students can now see and attempt it.");
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

    @PostMapping("/teacher/quizzes/{quizId}/unpublish")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unpublishQuiz(@PathVariable Long quizId, Authentication authentication) {
        String username = authentication.getName();
        User teacher = userService.getUserByUsername(username);

        Map<String, Object> response = new HashMap<>();

        if (teacher != null) {
            boolean success = quizService.unpublishQuiz(quizId, teacher.getId());
            if (success) {
                response.put("success", true);
                response.put("message", "Quiz unpublished successfully! Students can no longer see it.");
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

    @PostMapping("/teacher/quizzes/{quizId}/delete")
    public String deleteQuiz(@PathVariable Long quizId, Authentication authentication) {
        String username = authentication.getName();
        User teacher = userService.getUserByUsername(username);
        Quiz quiz = quizService.getQuizById(quizId).orElse(null);

        if (quiz != null && quiz.getTeacher().getId().equals(teacher.getId())) {
            quizService.deleteQuiz(quizId);
            return "redirect:/teacher/dashboard?success=deleted";
        }

        return "redirect:/teacher/dashboard?error=access_denied";
    }
}