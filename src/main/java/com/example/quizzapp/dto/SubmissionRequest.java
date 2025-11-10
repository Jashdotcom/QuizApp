package com.example.quizzapp.dto;

import java.util.List;

public class SubmissionRequest {
    private Long quizId;
    private List<AnswerDto> answers; // list of answers

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public List<AnswerDto> getAnswers() { return answers; }
    public void setAnswers(List<AnswerDto> answers) { this.answers = answers; }

    public static class AnswerDto {
        private Long questionId;
        private Integer selectedOptionIndex;
        private Integer timeTakenSeconds; // optional per-question time

        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }

        public Integer getSelectedOptionIndex() { return selectedOptionIndex; }
        public void setSelectedOptionIndex(Integer selectedOptionIndex) { this.selectedOptionIndex = selectedOptionIndex; }

        public Integer getTimeTakenSeconds() { return timeTakenSeconds; }
        public void setTimeTakenSeconds(Integer timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }
    }
}

