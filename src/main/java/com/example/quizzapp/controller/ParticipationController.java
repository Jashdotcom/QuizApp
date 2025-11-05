package com.example.quizzapp.controller;

import com.example.quizzapp.model.QuizParticipation;
import com.example.quizzapp.service.QuizParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/participation")
@CrossOrigin(origins = "http://localhost:3000")
public class ParticipationController {

    @Autowired
    private QuizParticipationService participationService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuizResults(@RequestBody SubmitResultsRequest request) {
        try {
            QuizParticipation participation = participationService.submitQuizResults(
                    request.getQuizId(),
                    request.getStudentId(),
                    request.getScore(),
                    request.getCorrectAnswers(),
                    request.getTimeTaken()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Quiz results submitted successfully!",
                    "participationId", participation.getId(),
                    "score", participation.getScore(),
                    "correctAnswers", participation.getCorrectAnswers(),
                    "totalQuestions", participation.getTotalQuestions(),
                    "percentage", participation.getPercentage()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/leaderboard/{quizId}")
    public ResponseEntity<?> getLeaderboard(@PathVariable Long quizId) {
        try {
            List<QuizParticipation> leaderboard = participationService.getLeaderboardForQuiz(quizId);
            return ResponseEntity.ok(leaderboard);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentResults(@PathVariable Long studentId) {
        try {
            List<QuizParticipation> results = participationService.getStudentResults(studentId);
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/quiz/{quizId}/participants")
    public ResponseEntity<?> getQuizParticipants(@PathVariable Long quizId) {
        try {
            List<QuizParticipation> participants = participationService.getQuizParticipants(quizId);
            return ResponseEntity.ok(participants);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/quiz/{quizId}/student/{studentId}")
    public ResponseEntity<?> getStudentQuizResult(@PathVariable Long quizId, @PathVariable Long studentId) {
        try {
            return participationService.getStudentQuizResult(quizId, studentId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/quiz/{quizId}/count")
    public ResponseEntity<?> getQuizParticipationCount(@PathVariable Long quizId) {
        try {
            Integer count = participationService.getQuizParticipationCount(quizId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Request class
    public static class SubmitResultsRequest {
        private Long quizId;
        private Long studentId;
        private Integer score;
        private Integer correctAnswers;
        private Integer timeTaken;

        // Getters and Setters
        public Long getQuizId() { return quizId; }
        public void setQuizId(Long quizId) { this.quizId = quizId; }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }

        public Integer getCorrectAnswers() { return correctAnswers; }
        public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }

        public Integer getTimeTaken() { return timeTaken; }
        public void setTimeTaken(Integer timeTaken) { this.timeTaken = timeTaken; }
    }
}