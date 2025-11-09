package com.example.quizzapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @NotBlank(message = "Question text is required")
    @Size(min = 5, max = 500, message = "Question must be between 5 and 500 characters")
    @Column(name = "question_text", nullable = false)
    private String questionText;

    // Keep it simple: store options as strings and mark correct index
    @NotEmpty(message = "At least one option is required")
    @Size(min = 2, max = 6, message = "Question must have between 2 and 6 options")
    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text", nullable = false)
    private List<String> options = new ArrayList<>();

    @Min(value = 0, message = "Correct option index must be non-negative")
    @Column(name = "correct_option_index", nullable = false)
    private int correctOptionIndex;

    @Min(value = 1, message = "Points must be at least 1")
    @Column(name = "points", nullable = false)
    private int points = 1;

    public Question() {}

    // getters & setters
    public Long getId() { return id; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(int correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
