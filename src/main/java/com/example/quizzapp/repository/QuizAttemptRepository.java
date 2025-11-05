package com.example.quizzapp.repository;

import com.example.quizzapp.model.QuizAttempt;
import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByQuiz(Quiz quiz);
    List<QuizAttempt> findByUser(User user);
}