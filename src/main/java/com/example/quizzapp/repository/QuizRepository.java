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

    // Basic quiz queries
    List<Quiz> findByCreatedBy(User createdBy);
    Optional<Quiz> findByJoinCode(String joinCode);
    List<Quiz> findByPublishedTrue();
    boolean existsByJoinCode(String joinCode);

    // Published quiz queries
    Optional<Quiz> findByIdAndPublishedTrue(Long quizId);
    List<Quiz> findByCreatedByAndPublishedTrue(User createdBy);
    Optional<Quiz> findByJoinCodeAndPublishedTrue(String joinCode);

    // Teacher-specific queries
    @Query("SELECT q FROM Quiz q WHERE q.createdBy.id = :teacherId AND q.published = true")
    List<Quiz> findPublishedQuizzesByTeacher(@Param("teacherId") Long teacherId);

    @Query("SELECT q FROM Quiz q WHERE q.createdBy.id = :teacherId")
    List<Quiz> findAllQuizzesByTeacher(@Param("teacherId") Long teacherId);

    // Active quiz queries
    @Query("SELECT q FROM Quiz q WHERE q.joinCode = :joinCode AND q.published = true")
    Optional<Quiz> findActiveQuizByJoinCode(@Param("joinCode") String joinCode);

    // Search and filter queries
    @Query("SELECT q FROM Quiz q WHERE q.published = true AND LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Quiz> findPublishedQuizzesByTitleContaining(@Param("keyword") String keyword);

    @Query("SELECT q FROM Quiz q WHERE q.createdBy.id = :teacherId AND LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Quiz> findQuizzesByTeacherAndTitleContaining(@Param("teacherId") Long teacherId, @Param("keyword") String keyword);

    // Count queries for statistics
    long countByCreatedBy(User createdBy);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.createdBy.id = :teacherId AND q.published = true")
    long countPublishedQuizzesByTeacher(@Param("teacherId") Long teacherId);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.published = true")
    long countAllPublishedQuizzes();

    // Recent quizzes queries
    @Query("SELECT q FROM Quiz q WHERE q.published = true ORDER BY q.createdAt DESC")
    List<Quiz> findRecentPublishedQuizzes();

    @Query("SELECT q FROM Quiz q WHERE q.createdBy.id = :teacherId ORDER BY q.createdAt DESC")
    List<Quiz> findRecentQuizzesByTeacher(@Param("teacherId") Long teacherId);

    // Check ownership and existence
    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END FROM Quiz q WHERE q.id = :quizId AND q.createdBy.id = :teacherId")
    boolean existsByIdAndTeacherId(@Param("quizId") Long quizId, @Param("teacherId") Long teacherId);

    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END FROM Quiz q WHERE q.joinCode = :joinCode AND q.published = true")
    boolean existsByJoinCodeAndPublishedTrue(@Param("joinCode") String joinCode);

    // Find quizzes with questions count (if you have Question entity)
    @Query("SELECT q, COUNT(quest) as questionCount FROM Quiz q LEFT JOIN q.questions quest WHERE q.id = :quizId GROUP BY q")
    Optional<Object[]> findQuizWithQuestionCount(@Param("quizId") Long quizId);

    // Find popular quizzes (by some metric - you can customize this)
    @Query("SELECT q FROM Quiz q WHERE q.published = true ORDER BY q.createdAt DESC")
    List<Quiz> findPopularPublishedQuizzes();
}