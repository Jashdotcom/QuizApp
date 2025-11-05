// src/main/java/com/example/quizzapp/service/QuizService.java
package com.example.quizzapp.service;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.quizzapp.model.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserService userService;

    private final Random random = new Random();

    public Quiz createQuiz(String title, String description, Long teacherId, Integer timePerQuestion) {
        User teacher = userService.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (teacher.getRole() != UserRole.TEACHER) {
            throw new RuntimeException("Only teachers can create quizzes");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setCreatedBy(teacher);
        quiz.setTimePerQuestion(timePerQuestion != null ? timePerQuestion : 30);
        quiz.setJoinCode(generateUniqueJoinCode());

        return quizRepository.save(quiz);
    }

    public Quiz publishQuiz(Long quizId, Long teacherId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(teacherId)) {
            throw new RuntimeException("You can only publish your own quizzes");
        }

        quiz.setPublished(true);
        return quizRepository.save(quiz);
    }

    public Quiz unpublishQuiz(Long quizId, Long teacherId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(teacherId)) {
            throw new RuntimeException("You can only unpublish your own quizzes");
        }

        quiz.setPublished(false);
        return quizRepository.save(quiz);
    }

    public Optional<Quiz> joinQuiz(String joinCode, Long studentId) {
        Optional<Quiz> quizOpt = quizRepository.findByJoinCodeAndPublishedTrue(joinCode);
        if (quizOpt.isPresent()) {
            userService.updateUserCurrentQuiz(studentId, joinCode);
        }
        return quizOpt;
    }

    public List<Quiz> getQuizzesByTeacher(Long teacherId) {
        User teacher = userService.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return quizRepository.findByCreatedBy(teacher);
    }

    public List<Quiz> getPublishedQuizzes() {
        return quizRepository.findByPublishedTrue();
    }

    public Optional<Quiz> getQuizByJoinCode(String joinCode) {
        return quizRepository.findByJoinCode(joinCode);
    }

    public Optional<Quiz> findById(Long quizId) {
        return quizRepository.findById(quizId);
    }

    private String generateUniqueJoinCode() {
        String code;
        do {
            code = String.format("%06d", random.nextInt(1000000));
        } while (quizRepository.existsByJoinCode(code));
        return code;
    }

    public void deleteQuiz(Long quizId, Long teacherId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(teacherId)) {
            throw new RuntimeException("You can only delete your own quizzes");
        }

        quizRepository.delete(quiz);
    }
    // Add these methods to your existing QuizService.java

    public List<Quiz> getTeacherQuizzes(User teacher) {
        return quizRepository.findByCreatedBy(teacher);
    }

    public Optional<Quiz> getQuizById(Long quizId) {
        return quizRepository.findById(quizId);
    }

    public Quiz save(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public Optional<Quiz> findQuizByCode(String code) {
        return quizRepository.findByJoinCode(code);
    }

    public Optional<Quiz> getPublishedQuizById(Long quizId) {
        return quizRepository.findByIdAndPublishedTrue(quizId);
    }

    public List<Quiz> getPublishedQuizzesByTeacher(Long teacherId) {
        User teacher = userService.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return quizRepository.findByCreatedByAndPublishedTrue(teacher);
    }
    public List<Quiz> getAllPublishedQuizzes() {
        return quizRepository.findByPublishedTrue();
    }

    public Quiz addQuestionToQuiz(Long quizId, String questionText, List<String> options, int correctOptionIndex, int points) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Create question logic here
        // You'll need to implement this based on your Question entity
        // This is a placeholder - implement based on your actual Question class
        return quizRepository.save(quiz);

    }
}