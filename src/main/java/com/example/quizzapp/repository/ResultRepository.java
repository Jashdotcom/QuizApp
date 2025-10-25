package com.example.quizzapp.repository;

import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUser(User user);
    List<QuizResult> findByQuizId(Long quizId);
    List<QuizResult> findByUserId(Long userId);

    // Remove any invalid count() methods and replace with:
    long countByUser(User user);

    @Query("SELECT COUNT(r) FROM QuizResult r WHERE r.quiz.id = :quizId")
    long countByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT AVG(r.score) FROM QuizResult r WHERE r.user = :user")
    Double findAverageScoreByUser(@Param("user") User user);
}