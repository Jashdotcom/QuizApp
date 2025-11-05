package com.example.quizzapp.repository;


import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCreatedBy(User createdBy);
    Optional<Quiz> findByJoinCode(String joinCode);
    List<Quiz> findByPublishedTrue();

    @Query("SELECT q FROM Quiz q WHERE q.createdBy.id = :teacherId AND q.published = true")
    List<Quiz> findPublishedQuizzesByTeacher(@Param("teacherId") Long teacherId);

    @Query("SELECT q FROM Quiz q WHERE q.joinCode = :joinCode AND q.published = true")
    Optional<Quiz> findActiveQuizByJoinCode(@Param("joinCode") String joinCode);

    boolean existsByJoinCode(String joinCode);


    Optional<Quiz> findByIdAndPublishedTrue(Long quizId);
    List<Quiz> findByCreatedByAndPublishedTrue(User createdBy);
    Optional<Quiz> findByJoinCodeAndPublishedTrue(String joinCode);
}