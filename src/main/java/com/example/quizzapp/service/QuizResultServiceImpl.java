// java
package com.example.quizzapp.service;

import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.model.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class QuizResultServiceImpl implements QuizResultService {

    @Override
    public Double getAverageScoreByStudent(User student) {
        // placeholder implementation - return 0.0 when no real data source is wired yet
        return 0.0;
    }

    @Override
    public List<QuizResult> getResultsByStudent(User student) {
        // placeholder - return empty list
        return Collections.emptyList();
    }

    @Override
    public List<Object[]> getLeaderboard() {
        // placeholder - return empty list
        return Collections.emptyList();
    }

    @Override
    public List<Object[]> getLeaderboard(Long quizId) {
        // placeholder - return empty list
        return Collections.emptyList();
    }
}
