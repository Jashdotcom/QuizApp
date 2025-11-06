package com.example.quizzapp.service;

import com.example.quizzapp.model.Question;
import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.QuestionRepository;
import com.example.quizzapp.repository.QuizRepository;
import com.example.quizzapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
public class QuizService {

    @Autowired private QuizRepository quizRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private UserRepository userRepository;

    // ===== Utilities =====
    private static final String CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RNG = new SecureRandom();

    private String generateJoinCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(CODE_ALPHABET.charAt(RNG.nextInt(CODE_ALPHABET.length())));
        return sb.toString();
    }

    // ===== CRUD =====

    public Quiz createQuiz(Long teacherId, String title, String description, Integer timePerQuestion) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setCreatedBy(teacher);
        quiz.setTimePerQuestion(timePerQuestion);
        quiz.setPublished(false);
        quiz.setJoinCode(generateJoinCode(6));

        return quizRepository.save(quiz);
    }

    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    public Optional<Quiz> getPublishedQuizById(Long id) {
        return quizRepository.findByIdAndPublishedTrue(id);
    }

    public Optional<Quiz> findQuizByCode(String code) {
        return quizRepository.findByJoinCode(code);
    }

    public Optional<Quiz> findPublishedByCode(String code) {
        return quizRepository.findByJoinCodeAndPublishedTrue(code);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public List<Quiz> getAllPublishedQuizzes() {
        return quizRepository.findByPublishedTrue();
    }

    public List<Quiz> getPublishedQuizzesByTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return quizRepository.findByCreatedByAndPublishedTrue(teacher);
    }

    public List<Quiz> getAvailableQuizzes() {
        return quizRepository.findByPublishedTrue();
    }

    public Quiz publishQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setPublished(true);
        return quizRepository.save(quiz);
    }

    public Quiz unpublishQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setPublished(false);
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long quizId) {
        quizRepository.deleteById(quizId);
    }

    // ===== Questions =====

    public Question addQuestionToQuiz(Long quizId,
                                      String questionText,
                                      List<String> options,
                                      int correctOptionIndex) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (options == null || options.size() < 2) {
            throw new IllegalArgumentException("At least 2 options required");
        }
        if (correctOptionIndex < 0 || correctOptionIndex >= options.size()) {
            throw new IllegalArgumentException("Correct option index out of range");
        }

        Question q = new Question();
        q.setQuiz(quiz);
        q.setQuestionText(questionText);
        q.setOptions(new ArrayList<>(options));
        q.setCorrectOptionIndex(correctOptionIndex);

        Question saved = questionRepository.save(q);
        // keep owning side consistent
        quiz.getQuestions().add(saved);
        quizRepository.save(quiz);

        return saved;
    }

    // Students “join” quiz by code (just returns the published quiz if code valid)
    public Quiz joinQuiz(String joinCode) {
        return quizRepository.findByJoinCodeAndPublishedTrue(joinCode)
                .orElseThrow(() -> new RuntimeException("Invalid code or quiz not published"));
    }
}
