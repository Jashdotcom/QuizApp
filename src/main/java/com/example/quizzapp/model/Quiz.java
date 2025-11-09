package com.example.quizzapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Column(name = "join_code", unique = true)
    private String joinCode;

    // For backwards compatibility - alias for joinCode
    @Column(name = "unique_code", unique = true)
    private String uniqueCode;

    @Column(name = "published")
    private boolean published;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private QuizStatus status = QuizStatus.DRAFT;

    @Column(name = "time_per_question")
    private Integer timePerQuestion;

    @Column(name = "published_at")
    private ZonedDateTime publishedAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @Column(name = "created_at")
    private ZonedDateTime createdAt = ZonedDateTime.now();

    // ✅ Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Integer getTimePerQuestion() {
        return timePerQuestion;
    }

    public void setTimePerQuestion(Integer timePerQuestion) {
        this.timePerQuestion = timePerQuestion;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUniqueCode() {
        // Return uniqueCode if set, otherwise return joinCode for backwards compatibility
        return uniqueCode != null ? uniqueCode : joinCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
        // Keep joinCode in sync for backwards compatibility
        if (this.joinCode == null) {
            this.joinCode = uniqueCode;
        }
    }

    public QuizStatus getStatus() {
        return status;
    }

    public void setStatus(QuizStatus status) {
        this.status = status;
    }

    public ZonedDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(ZonedDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
