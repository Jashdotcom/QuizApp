package com.example.quizzapp.repository;


import com.example.quizzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.quizzapp.model.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // New methods for role-based queries
    List<User> findByRole(UserRole role);
    List<User> findByRoleAndCurrentQuizCode(    UserRole role, String quizCode);

    @Query("SELECT u FROM User u WHERE u.role = 'TEACHER' AND u.id = :teacherId")
    Optional<User> findTeacherById(@Param("teacherId") Long teacherId);
}