package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.QuizStatus;
import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.QuizRepository;
import com.example.quizzapp.repository.UserRepository;
import com.example.quizzapp.security.UserPrincipal;
import com.example.quizzapp.service.QuizResultService;
import com.example.quizzapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizResultService quizResultService;

    @Autowired
    private QuizService quizService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        // Get the current teacher user
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User teacher = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Quiz> quizzes = quizRepository.findByCreatedBy(teacher);
        List<Quiz> publishedQuizzes = quizzes.stream()
                .filter(Quiz::isPublished)
                .collect(Collectors.toList());
        
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("publishedQuizzes", publishedQuizzes);
        model.addAttribute("totalQuizzes", quizzes.size());
        model.addAttribute("publishedCount", publishedQuizzes.size());
        model.addAttribute("teacherName", teacher.getUsername());
        model.addAttribute("username", teacher.getUsername());
        return "teacher-dashboard";
    }

    @GetMapping("/create")
    public String createQuizPage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        return "create-quiz";
    }

    @GetMapping("/quizzes/create")
    public String createQuizPageAlias(Authentication authentication, Model model) {
        return createQuizPage(authentication, model);
    }

    @GetMapping("/leaderboard")
    public String viewGlobalLeaderboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User teacher = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("teacherName", teacher.getUsername());
        model.addAttribute("leaderboard", List.of()); // Would need proper implementation
        return "teacher-leaderboard";
    }

    @GetMapping("/quizzes/{quizId}/students")
    public String viewQuizStudents(@PathVariable Long quizId, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("results", quizResultService.getLeaderboard(quizId));
        return "teacher-quiz-students";
    }

    @GetMapping("/quizzes/{quizId}/leaderboard")
    public String viewQuizLeaderboard(@PathVariable Long quizId, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("leaderboard", quizResultService.getLeaderboard(quizId));
        return "teacher-quiz-leaderboard";
    }

    @PostMapping("/create")
    public String createQuiz(@RequestParam String title,
                             @RequestParam String description,
                             Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        // Get the current teacher user
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User teacher = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDescription(description);
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        quiz.setJoinCode(code);
        quiz.setUniqueCode(code);
        quiz.setStatus(QuizStatus.DRAFT);
        quiz.setCreatedBy(teacher);
        quizRepository.save(quiz);

        return "redirect:/teacher/dashboard";
    }

    @GetMapping("/leaderboard/{quizId}")
    public String viewLeaderboard(@PathVariable Long quizId, Model model) {
        model.addAttribute("leaderboard", quizResultService.getLeaderboard(quizId));
        return "leaderboard";
    }

    // REST API endpoints for AJAX calls
    @PostMapping("/quizzes/{quizId}/publish")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> publishQuizApi(@PathVariable Long quizId, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }

            Quiz quiz = quizService.publishQuiz(quizId);
            response.put("success", true);
            response.put("message", "Quiz published successfully!");
            response.put("quizId", quiz.getId());
            response.put("status", quiz.getStatus().name());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to publish quiz: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/quizzes/{quizId}/unpublish")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unpublishQuizApi(@PathVariable Long quizId, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }

            Quiz quiz = quizService.unpublishQuiz(quizId);
            response.put("success", true);
            response.put("message", "Quiz unpublished successfully!");
            response.put("quizId", quiz.getId());
            response.put("status", quiz.getStatus().name());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to unpublish quiz: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/quizzes/{quizId}/duplicate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> duplicateQuizApi(@PathVariable Long quizId, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Quiz duplicatedQuiz = quizService.duplicateQuiz(quizId, userPrincipal.getId());
            
            response.put("success", true);
            response.put("message", "Quiz duplicated successfully!");
            response.put("quizId", duplicatedQuiz.getId());
            response.put("title", duplicatedQuiz.getTitle());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to duplicate quiz: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}