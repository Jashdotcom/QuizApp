package com.example.quizzapp.repository;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.QuizParticipation;
import com.example.quizzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizParticipationRepository extends JpaRepository<QuizParticipation, Long> {
    List<QuizParticipation> findByStudent(User student);
    List<QuizParticipation> findByQuiz(Quiz quiz);
    Optional<QuizParticipation> findByQuizAndStudent(Quiz quiz, User student);

    @Query("SELECT qp FROM QuizParticipation qp WHERE qp.quiz.id = :quizId ORDER BY qp.score DESC, qp.timeTaken ASC")
    List<QuizParticipation> findLeaderboardByQuiz(@Param("quizId") Long quizId);

    @Query("SELECT qp FROM QuizParticipation qp WHERE qp.student.id = :studentId ORDER BY qp.participatedAt DESC")
    List<QuizParticipation> findRecentParticipationsByStudent(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(qp) FROM QuizParticipation qp WHERE qp.quiz.id = :quizId")
    Integer countParticipationsByQuiz(@Param("quizId") Long quizId);
}