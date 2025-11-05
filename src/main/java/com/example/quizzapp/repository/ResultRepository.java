package com.example.quizzapp.repository;

import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<QuizResult, Long> {

    // Basic result queries
    List<QuizResult> findByUser(User user);
    List<QuizResult> findByQuizId(Long quizId);
    List<QuizResult> findByUserId(Long userId);

    // Count queries
    long countByUser(User user);

    @Query("SELECT COUNT(r) FROM QuizResult r WHERE r.quiz.id = :quizId")
    long countByQuizId(@Param("quizId") Long quizId);

    // Average score queries
    @Query("SELECT AVG(r.score) FROM QuizResult r WHERE r.user = :user")
    Double findAverageScoreByUser(@Param("user") User user);

    @Query("SELECT AVG(r.score) FROM QuizResult r WHERE r.quiz.id = :quizId")
    Double findAverageScoreByQuizId(@Param("quizId") Long quizId);

    // Find specific result for a user and quiz
    @Query("SELECT r FROM QuizResult r WHERE r.user.id = :userId AND r.quiz.id = :quizId")
    Optional<QuizResult> findByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // Find top scores for a quiz
    @Query("SELECT r FROM QuizResult r WHERE r.quiz.id = :quizId ORDER BY r.score DESC, r.completedAt ASC")
    List<QuizResult> findTopScoresByQuizId(@Param("quizId") Long quizId);

    // Find user's recent results
    @Query("SELECT r FROM QuizResult r WHERE r.user.id = :userId ORDER BY r.completedAt DESC")
    List<QuizResult> findRecentResultsByUserId(@Param("userId") Long userId);

    // Find quiz results with pagination (top N)
    @Query("SELECT r FROM QuizResult r WHERE r.quiz.id = :quizId ORDER BY r.score DESC")
    List<QuizResult> findTopNByQuizId(@Param("quizId") Long quizId, org.springframework.data.domain.Pageable pageable);

    // Statistics for a quiz
    @Query("SELECT MAX(r.score) FROM QuizResult r WHERE r.quiz.id = :quizId")
    Double findMaxScoreByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT MIN(r.score) FROM QuizResult r WHERE r.quiz.id = :quizId")
    Double findMinScoreByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT COUNT(r) FROM QuizResult r WHERE r.quiz.id = :quizId AND r.score >= :passingScore")
    long countPassedStudentsByQuizId(@Param("quizId") Long quizId, @Param("passingScore") Double passingScore);

    // Student performance analytics
    @Query("SELECT r.quiz.title, r.score, r.completedAt FROM QuizResult r WHERE r.user.id = :userId ORDER BY r.completedAt DESC")
    List<Object[]> findStudentPerformanceHistory(@Param("userId") Long userId);

    // Teacher analytics - results for teacher's quizzes
    @Query("SELECT r FROM QuizResult r WHERE r.quiz.createdBy.id = :teacherId ORDER BY r.completedAt DESC")
    List<QuizResult> findResultsByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT COUNT(r) FROM QuizResult r WHERE r.quiz.createdBy.id = :teacherId")
    long countResultsByTeacherId(@Param("teacherId") Long teacherId);

    // Check if user has already taken a quiz
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM QuizResult r WHERE r.user.id = :userId AND r.quiz.id = :quizId")
    boolean existsByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // Find results within a score range
    @Query("SELECT r FROM QuizResult r WHERE r.quiz.id = :quizId AND r.score BETWEEN :minScore AND :maxScore")
    List<QuizResult> findByQuizIdAndScoreBetween(@Param("quizId") Long quizId,
                                                 @Param("minScore") Double minScore,
                                                 @Param("maxScore") Double maxScore);

    // Get completion rate for a quiz
    @Query("SELECT COUNT(r) FROM QuizResult r WHERE r.quiz.id = :quizId")
    long getCompletionCount(@Param("quizId") Long quizId);

    // Find results by quiz and order by completion time
    @Query("SELECT r FROM QuizResult r WHERE r.quiz.id = :quizId ORDER BY r.completedAt ASC")
    List<QuizResult> findByQuizIdOrderByCompletionTime(@Param("quizId") Long quizId);
}