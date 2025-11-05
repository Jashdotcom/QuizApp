package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import com.example.quizzapp.service.QuizService;
import com.example.quizzapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    // Get all quizzes for a teacher
    @GetMapping("/{teacherId}/quizzes")
    public ResponseEntity<?> getTeacherQuizzes(@PathVariable Long teacherId) {
        try {
            Optional<User> teacher = userService.findById(teacherId);
            if (teacher.isEmpty()) {
                return ResponseEntity.badRequest().body("Teacher not found");
            }
            List<Quiz> quizzes = quizService.getTeacherQuizzes(teacher.get());
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching quizzes: " + e.getMessage());
        }
    }

    // Create a new quiz
    @PostMapping("/{teacherId}/quizzes")
    public ResponseEntity<?> createQuiz(@PathVariable Long teacherId, @RequestBody CreateQuizRequest request) {
        try {
            Quiz quiz = quizService.createQuiz(
                    request.getTitle(),
                    request.getDescription(),
                    teacherId,
                    request.getTimePerQuestion()
            );
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating quiz: " + e.getMessage());
        }
    }

    // Publish a quiz
    @PostMapping("/quizzes/{quizId}/publish")
    public ResponseEntity<?> publishQuiz(@PathVariable Long quizId, @RequestBody PublishRequest request) {
        try {
            Quiz quiz = quizService.publishQuiz(quizId, request.getTeacherId());
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error publishing quiz: " + e.getMessage());
        }
    }

    // Unpublish a quiz
    @PostMapping("/quizzes/{quizId}/unpublish")
    public ResponseEntity<?> unpublishQuiz(@PathVariable Long quizId, @RequestBody PublishRequest request) {
        try {
            Quiz quiz = quizService.unpublishQuiz(quizId, request.getTeacherId());
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error unpublishing quiz: " + e.getMessage());
        }
    }

    // Get quiz by ID
    @GetMapping("/quizzes/{quizId}")
    public ResponseEntity<?> getQuizById(@PathVariable Long quizId) {
        try {
            Optional<Quiz> quiz = quizService.getQuizById(quizId);
            if (quiz.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(quiz.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching quiz: " + e.getMessage());
        }
    }

    // Delete quiz
    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId, @RequestBody DeleteQuizRequest request) {
        try {
            quizService.deleteQuiz(quizId, request.getTeacherId());
            return ResponseEntity.ok("Quiz deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting quiz: " + e.getMessage());
        }
    }

    // Add question to quiz
    @PostMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<?> addQuestionToQuiz(@PathVariable Long quizId, @RequestBody AddQuestionRequest request) {
        try {
            Quiz quiz = quizService.addQuestionToQuiz(
                    quizId,
                    request.getQuestionText(),
                    request.getOptions(),
                    request.getCorrectOptionIndex(),
                    request.getPoints()
            );
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding question: " + e.getMessage());
        }
    }

    // Request DTO classes
    public static class CreateQuizRequest {
        private String title;
        private String description;
        private Integer timePerQuestion;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getTimePerQuestion() { return timePerQuestion; }
        public void setTimePerQuestion(Integer timePerQuestion) { this.timePerQuestion = timePerQuestion; }
    }

    public static class PublishRequest {
        private Long teacherId;

        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    }

    public static class DeleteQuizRequest {
        private Long teacherId;

        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    }

    public static class AddQuestionRequest {
        private String questionText;
        private List<String> options;
        private int correctOptionIndex;
        private int points;

        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
        public int getCorrectOptionIndex() { return correctOptionIndex; }
        public void setCorrectOptionIndex(int correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }
        public int getPoints() { return points; }
        public void setPoints(int points) { this.points = points; }
    }
}