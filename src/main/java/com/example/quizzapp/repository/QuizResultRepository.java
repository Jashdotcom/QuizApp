package com.example.quizzapp.repository;

import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByStudent(User student);

    @Query("SELECT r.student.username, AVG(r.score) as avgScore FROM QuizResult r GROUP BY r.student.id ORDER BY avgScore DESC")
    List<Object[]> findLeaderboardAll();

    @Query("SELECT r.student.username, AVG(r.score) as avgScore FROM QuizResult r WHERE r.quiz.id = :quizId GROUP BY r.student.id ORDER BY avgScore DESC")
    List<Object[]> findLeaderboardByQuiz(Long quizId);
}
