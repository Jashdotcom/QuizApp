package com.example.quizzapp.service;

import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Service
public class QuizResultServiceImpl implements QuizResultService {

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Override
    public Double getAverageScoreByStudent(User student) {
        List<QuizResult> results = quizResultRepository.findByStudent(student);
        if (results == null || results.isEmpty()) {
            return 0.0;
        }
        OptionalDouble avg = results.stream()
                .mapToDouble(r -> (double) r.getScore())
                .average();
        return avg.isPresent() ? avg.getAsDouble() : 0.0;
    }

    @Override
    public List<QuizResult> getResultsByStudent(User student) {
        List<QuizResult> results = quizResultRepository.findByStudent(student);
        return results != null ? results : Collections.emptyList();
    }

    @Override
    public List<Object[]> getLeaderboard() {
        List<Object[]> rows = quizResultRepository.findLeaderboardAll();
        return rows != null ? rows : Collections.emptyList();
    }

    @Override
    public List<Object[]> getLeaderboard(Long quizId) {
        List<Object[]> rows = quizResultRepository.findLeaderboardByQuiz(quizId);
        return rows != null ? rows : Collections.emptyList();
    }

    @Override
    public QuizResult saveQuizResult(QuizResult result) {
        return quizResultRepository.save(result);
    }

    @Override
    public Optional<QuizResult> findById(Long id) {
        return quizResultRepository.findById(id);
    }
}
