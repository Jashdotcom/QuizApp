package com.example.quizzapp.controller;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.quizzapp.model.UserRole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "http://localhost:3000")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@RequestBody CreateQuizRequest request) {
        try {
            Quiz quiz = quizService.createQuiz(
                    request.getTitle(),
                    request.getDescription(),
                    request.getTeacherId(),
                    request.getTimePerQuestion()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quiz created successfully!");
            response.put("quizId", quiz.getId());
            response.put("joinCode", quiz.getJoinCode());
            response.put("published", quiz.isPublished());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{quizId}/publish")
    public ResponseEntity<?> publishQuiz(@PathVariable Long quizId, @RequestBody Map<String, Long> request) {
        try {
            Long teacherId = request.get("teacherId");
            Quiz quiz = quizService.publishQuiz(quizId, teacherId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quiz published successfully!");
            response.put("quizId", quiz.getId());
            response.put("joinCode", quiz.getJoinCode());
            response.put("published", quiz.isPublished());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{quizId}/unpublish")
    public ResponseEntity<?> unpublishQuiz(@PathVariable Long quizId, @RequestBody Map<String, Long> request) {
        try {
            Long teacherId = request.get("teacherId");
            Quiz quiz = quizService.unpublishQuiz(quizId, teacherId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quiz unpublished successfully!");
            response.put("quizId", quiz.getId());
            response.put("published", quiz.isPublished());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinQuiz(@RequestBody JoinQuizRequest request) {
        try {
            Optional<Quiz> quiz = quizService.joinQuiz(request.getJoinCode(), request.getStudentId());

            if (quiz.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Successfully joined quiz!");
                response.put("quizId", quiz.get().getId());
                response.put("title", quiz.get().getTitle());
                response.put("description", quiz.get().getDescription());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Invalid join code or quiz not published");
            }

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<?> getTeacherQuizzes(@PathVariable Long teacherId) {
        try {
            List<Quiz> quizzes = quizService.getQuizzesByTeacher(teacherId);
            return ResponseEntity.ok(quizzes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/published/teacher/{teacherId}")
    public ResponseEntity<?> getPublishedTeacherQuizzes(@PathVariable Long teacherId) {
        try {
            List<Quiz> quizzes = quizService.getPublishedQuizzesByTeacher(teacherId);
            return ResponseEntity.ok(quizzes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/published")
    public ResponseEntity<?> getAllPublishedQuizzes() {
        List<Quiz> quizzes = quizService.getAllPublishedQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<?> addQuestion(@PathVariable Long quizId, @RequestBody AddQuestionRequest request) {
        try {
            Quiz quiz = quizService.addQuestionToQuiz(
                    quizId,
                    request.getQuestionText(),
                    request.getOptions(),
                    request.getCorrectOptionIndex(),
                    request.getPoints()
            );

            return ResponseEntity.ok("Question added successfully!");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId, @RequestBody Map<String, Long> request) {
        try {
            Long teacherId = request.get("teacherId");
            quizService.deleteQuiz(quizId, teacherId);
            return ResponseEntity.ok("Quiz deleted successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Request classes
    public static class CreateQuizRequest {
        private String title;
        private String description;
        private Long teacherId;
        private Integer timePerQuestion;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

        public Integer getTimePerQuestion() { return timePerQuestion; }
        public void setTimePerQuestion(Integer timePerQuestion) { this.timePerQuestion = timePerQuestion; }
    }

    public static class JoinQuizRequest {
        private String joinCode;
        private Long studentId;

        // Getters and Setters
        public String getJoinCode() { return joinCode; }
        public void setJoinCode(String joinCode) { this.joinCode = joinCode; }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
    }

    public static class AddQuestionRequest {
        private String questionText;
        private List<String> options;
        private int correctOptionIndex;
        private int points;

        // Getters and Setters
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