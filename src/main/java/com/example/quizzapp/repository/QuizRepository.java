package com.example.quizzapp.repository;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Optional<Quiz> findByJoinCode(String joinCode);
    Optional<Quiz> findByJoinCodeAndPublishedTrue(String joinCode);
    Optional<Quiz> findByIdAndPublishedTrue(Long id);
    List<Quiz> findByCreatedBy(User createdBy);
    List<Quiz> findByCreatedByAndPublishedTrue(User createdBy);
    List<Quiz> findByPublishedTrue();
}
