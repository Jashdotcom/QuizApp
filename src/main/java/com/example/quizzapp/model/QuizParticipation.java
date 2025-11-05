package com.example.quizzapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_participations")
public class QuizParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    private Integer score = 0;

    private Integer correctAnswers = 0;

    private Integer totalQuestions = 0;

    private LocalDateTime participatedAt;

    private Integer timeTaken = 0; // in seconds

    @PrePersist
    protected void onCreate() {
        participatedAt = LocalDateTime.now();
    }

    // Constructors
    public QuizParticipation() {}

    public QuizParticipation(Quiz quiz, User student) {
        this.quiz = quiz;
        this.student = student;
        this.totalQuestions = quiz.getQuestions().size();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public LocalDateTime getParticipatedAt() { return participatedAt; }
    public void setParticipatedAt(LocalDateTime participatedAt) { this.participatedAt = participatedAt; }

    public Integer getTimeTaken() { return timeTaken; }
    public void setTimeTaken(Integer timeTaken) { this.timeTaken = timeTaken; }

    // Helper method to calculate percentage
    public Double getPercentage() {
        if (totalQuestions == 0) return 0.0;
        return (correctAnswers * 100.0) / totalQuestions;
    }
}