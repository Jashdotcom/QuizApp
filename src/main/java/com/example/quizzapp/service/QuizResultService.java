package com.example.quizzapp.service;

import com.example.quizzapp.model.*;
import com.example.quizzapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizResultService {

    @Autowired
    private ResultRepository quizResultRepository;

    @Autowired
    private QuizRepository quizRepository;

    public QuizResult saveResult(Quiz quiz, User student, int score, int correctAnswers, int totalQuestions, int timeTaken) {
        QuizResult result = new QuizResult();
        result.setQuiz(quiz);
        result.setStudent(student);
        result.setScore(score);
        result.setCorrectAnswers(correctAnswers);
        result.setTotalQuestions(totalQuestions);
        result.setTimeTaken(timeTaken);
        return quizResultRepository.save(result);
    }

    public List<QuizResult> getResultsByStudent(User student) {
        return quizResultRepository.findByStudent(student);
    }

    public List<QuizResult> getLeaderboard(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        return quizResultRepository.findByQuizOrderByScoreDesc(quiz);
    }
}
