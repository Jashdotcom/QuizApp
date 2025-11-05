package com.example.quizzapp.service;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import com.example.quizzapp.model.QuizAttempt;
import com.example.quizzapp.repository.QuizAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizAttemptService {
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    public QuizAttempt studentAttemptQuiz(User user, Quiz quiz, int score) {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setScore(score);
        return quizAttemptRepository.save(attempt);
    }

    public List<QuizAttempt> getAttemptsByQuiz(Quiz quiz) {
        return quizAttemptRepository.findByQuiz(quiz);
    }

    public List<QuizAttempt> getAttemptsByUser(User user) {
        return quizAttemptRepository.findByUser(user);
    }
}