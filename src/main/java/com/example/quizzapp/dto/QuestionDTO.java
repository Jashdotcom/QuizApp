package com.example.quizzapp.dto;


import com.example.quizzapp.model.QuestionType;

import java.util.List;

public class QuestionDTO {
    private Long id;
    private String questionText;
    private List<String> options;
    private Integer correctAnswer;
    private QuestionType questionType;

    // Constructors
    public QuestionDTO() {}

    public QuestionDTO(String questionText, List<String> options, Integer correctAnswer, QuestionType questionType) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.questionType = questionType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public Integer getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(Integer correctAnswer) { this.correctAnswer = correctAnswer; }

    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }
}