package com.example.quizzapp.service;

import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.model.User;

import java.util.List;
import java.util.Optional;

public interface QuizResultService {
    Double getAverageScoreByStudent(User student);
    List<QuizResult> getResultsByStudent(User student);

    /**
     * Leaderboard across all quizzes
     */
    List<Object[]> getLeaderboard();

    /**
     * Leaderboard for a specific quiz
     */
    List<Object[]> getLeaderboard(Long quizId);

    // Persist a QuizResult
    QuizResult saveQuizResult(QuizResult result);

    // Lookup
    Optional<QuizResult> findById(Long id);
}
