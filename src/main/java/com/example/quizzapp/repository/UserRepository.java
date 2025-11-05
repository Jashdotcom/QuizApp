package com.example.quizzapp.repository;

import com.example.quizzapp.model.User;
import com.example.quizzapp.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Basic user queries
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // Role-based queries
    List<User> findByRole(UserRole role);
    List<User> findByRoleAndCurrentQuizCode(UserRole role, String quizCode);

    // Teacher-specific queries
    @Query("SELECT u FROM User u WHERE u.role = 'TEACHER' AND u.id = :teacherId")
    Optional<User> findTeacherById(@Param("teacherId") Long teacherId);

    // Student-specific queries
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.id = :studentId")
    Optional<User> findStudentById(@Param("studentId") Long studentId);

    // Find users by current quiz code (regardless of role)
    List<User> findByCurrentQuizCode(String quizCode);

    // Find students not enrolled in any quiz
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.currentQuizCode IS NULL")
    List<User> findStudentsWithoutQuiz();

    // Find students enrolled in a specific quiz
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.currentQuizCode = :quizCode")
    List<User> findStudentsByQuizCode(@Param("quizCode") String quizCode);

    // Count queries for statistics
    long countByRole(UserRole role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'STUDENT' AND u.currentQuizCode = :quizCode")
    long countStudentsByQuizCode(@Param("quizCode") String quizCode);

    // Search users by username pattern
    List<User> findByUsernameContainingIgnoreCase(String username);

    // Search students by username pattern
    List<User> findByRoleAndUsernameContainingIgnoreCase(UserRole role, String username);

    // Find users with email pattern (for admin purposes)
    List<User> findByEmailContainingIgnoreCase(String email);

    // Check if a student is enrolled in any quiz
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :studentId AND u.role = 'STUDENT' AND u.currentQuizCode IS NOT NULL")
    boolean isStudentEnrolledInQuiz(@Param("studentId") Long studentId);

    // Find teacher by username (for login/authentication)
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.role = 'TEACHER'")
    Optional<User> findTeacherByUsername(@Param("username") String username);

    // Find student by username (for login/authentication)
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.role = 'STUDENT'")
    Optional<User> findStudentByUsername(@Param("username") String username);
}