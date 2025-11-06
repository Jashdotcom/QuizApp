package com.example.quizzapp.repository;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByStudent(User student);
    List<QuizResult> findByQuizOrderByScoreDesc(Quiz quiz);
}
