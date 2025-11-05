package com.example.quizzapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer score = 0;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // STUDENT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswer> userAnswers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }
    public void setUser(User user) { this.user = user; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    public void setScore(int score) { this.score = score; }
    public int getScore() { return score; }
}