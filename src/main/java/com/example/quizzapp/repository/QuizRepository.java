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
    List<Quiz> findByTeacher(User teacher);
    List<Quiz> findByPublishedTrue();
    List<Quiz> findByPublishedTrueOrderByPublishedAtDesc();
    List<Quiz> findByTeacherAndPublished(User teacher, boolean published);
    Optional<Quiz> findByIdAndPublishedTrue(Long id);
    Optional<Quiz> findByIdAndTeacher(Long id, User teacher);
    long countByTeacher(User teacher);
    long countByTeacherAndPublished(User teacher, boolean published);

    @Query("SELECT q FROM Quiz q WHERE q.published = true ORDER BY q.publishedAt DESC")
    List<Quiz> findRecentPublishedQuizzes(@Param("limit") int limit);

    @Query("SELECT q FROM Quiz q WHERE q.id = :quizId AND q.teacher = :teacher")
    Optional<Quiz> findByQuizIdAndTeacher(@Param("quizId") Long quizId, @Param("teacher") User teacher);
}