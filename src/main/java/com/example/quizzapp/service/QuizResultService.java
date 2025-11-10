package com.example.quizzapp.service;

import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.model.User;

import java.util.List;

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
}
