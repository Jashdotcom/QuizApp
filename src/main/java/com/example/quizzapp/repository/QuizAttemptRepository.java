package com.example.quizzapp.repository;

import com.example.quizzapp.model.QuizAttempt;
import com.example.quizzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUser(User user);
    List<QuizAttempt> findByQuizId(Long quizId);
    List<QuizAttempt> findByUserId(Long userId);
    long countByUser(User user);

    Optional<QuizAttempt> findByUserIdAndQuizId(Long userId, Long quizId);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz.id = :quizId ORDER BY qa.score DESC, qa.completedAt ASC")
    List<QuizAttempt> findLeaderboardByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT qa.user.username, qa.score, qa.quiz.title FROM QuizAttempt qa ORDER BY qa.score DESC, qa.completedAt ASC")
    List<Object[]> findGlobalLeaderboard();
}