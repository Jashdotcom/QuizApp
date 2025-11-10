package com.example.quizzapp.service;

import com.example.quizzapp.model.Question;
import com.example.quizzapp.model.Quiz;

import java.util.List;
import java.util.Optional;

public interface QuizService {
    List<Quiz> getAllQuizzes();
    Quiz getQuizByCode(String code);

    Optional<Quiz> getQuizById(Long id);
    Optional<Quiz> getPublishedQuizById(Long id);
    Optional<Quiz> findQuizByCode(String code);
    Optional<Quiz> findPublishedByCode(String code);

    List<Quiz> getAllPublishedQuizzes();
    List<Quiz> getPublishedQuizzesByTeacher(Long teacherId);
    List<Quiz> getAvailableQuizzes();

    Quiz publishQuiz(Long quizId);
    Quiz unpublishQuiz(Long quizId);
    void deleteQuiz(Long quizId);

    Question addQuestionToQuiz(Long quizId, String questionText, java.util.List<String> options, int correctOptionIndex);

    Quiz createQuiz(Long teacherId, String title, String description, Integer timePerQuestion);
    Quiz joinQuiz(String joinCode);
}
