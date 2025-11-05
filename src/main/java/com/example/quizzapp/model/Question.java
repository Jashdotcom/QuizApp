package com.example.quizzapp.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(nullable = false)
    private String questionText;

    private int points = 1;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    // Constructors
    public Question() {}

    public Question(String questionText, Quiz quiz) {
        this.questionText = questionText;
        this.quiz = quiz;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public List<Option> getOptions() { return options; }
    public void setOptions(List<Option> options) { this.options = options; }

    public void addOption(Option option) {
        options.add(option);
        option.setQuestion(this);
    }

    public void removeOption(Option option) {
        options.remove(option);
        option.setQuestion(null);
    }

    // Option inner class
    @Entity
    @Table(name = "question_options")
    public static class Option {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "question_id")
        private Question question;

        @Column(nullable = false)
        private String optionText;

        private boolean correct = false;

        // Constructors
        public Option() {}

        public Option(String optionText, boolean correct) {
            this.optionText = optionText;
            this.correct = correct;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Question getQuestion() { return question; }
        public void setQuestion(Question question) { this.question = question; }

        public String getOptionText() { return optionText; }
        public void setOptionText(String optionText) { this.optionText = optionText; }

        public boolean isCorrect() { return correct; }
        public void setCorrect(boolean correct) { this.correct = correct; }
    }
}