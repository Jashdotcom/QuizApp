package com.example.quizzapp.repository;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByJoinCode(String code);
    Optional<Quiz> findByJoinCodeAndPublishedTrue(String code);
    Optional<Quiz> findByIdAndPublishedTrue(Long id);
    List<Quiz> findByPublishedTrue();
    List<Quiz> findByCreatedByAndPublishedTrue(User teacher);
    List<Quiz> findByCreatedBy(User teacher);
}
