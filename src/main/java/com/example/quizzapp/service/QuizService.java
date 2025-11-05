package com.example.quizzapp.service;

import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import com.example.quizzapp.model.UserRole;
import com.example.quizzapp.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // ✅ Create a new quiz
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
        quiz.setPublished(false); // Default to unpublished
        quiz.setJoinCode(generateUniqueJoinCode());

        return quizRepository.save(quiz);
    }

    // ✅ Publish a quiz
    public Quiz publishQuiz(Long quizId, Long teacherId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(teacherId)) {
            throw new RuntimeException("You can only publish your own quizzes");
        }

        quiz.setPublished(true);
        return quizRepository.save(quiz);
    }

    // ✅ Unpublish a quiz
    public Quiz unpublishQuiz(Long quizId, Long teacherId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(teacherId)) {
            throw new RuntimeException("You can only unpublish your own quizzes");
        }

        quiz.setPublished(false);
        return quizRepository.save(quiz);
    }

    // ✅ Student joins a quiz
    public Optional<Quiz> joinQuiz(String joinCode, Long studentId) {
        Optional<Quiz> quizOpt = quizRepository.findByJoinCodeAndPublishedTrue(joinCode);
        if (quizOpt.isPresent()) {
            userService.updateUserCurrentQuiz(studentId, joinCode);
        }
        return quizOpt;
    }

    // ✅ Get all quizzes by teacher
    public List<Quiz> getQuizzesByTeacher(Long teacherId) {
        User teacher = userService.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return quizRepository.findByCreatedBy(teacher);
    }

    // ✅ Get published quizzes by teacher
    public List<Quiz> getPublishedQuizzesByTeacher(Long teacherId) {
        User teacher = userService.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return quizRepository.findByCreatedByAndPublishedTrue(teacher);
    }

    // ✅ Get all published quizzes
    public List<Quiz> getPublishedQuizzes() {
        return quizRepository.findByPublishedTrue();
    }

    // ✅ Get quiz by join code (any status)
    public Optional<Quiz> getQuizByJoinCode(String joinCode) {
        return quizRepository.findByJoinCode(joinCode);
    }

    // ✅ Get published quiz by join code
    public Optional<Quiz> getPublishedQuizByJoinCode(String joinCode) {
        return quizRepository.findByJoinCodeAndPublishedTrue(joinCode);
    }

    // ✅ Find quiz by ID
    public Optional<Quiz> findById(Long quizId) {
        return quizRepository.findById(quizId);
    }

    // ✅ Get quiz by ID or throw exception
    public Quiz findByIdOrThrow(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
    }

    // ✅ Get published quiz by ID
    public Optional<Quiz> getPublishedQuizById(Long quizId) {
        return quizRepository.findByIdAndPublishedTrue(quizId);
    }

    // ✅ Get teacher's quizzes
    public List<Quiz> getTeacherQuizzes(User teacher) {
        return quizRepository.findByCreatedBy(teacher);
    }

    // ✅ Save quiz
    public Quiz save(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    // ✅ Find quiz by code (alias for getQuizByJoinCode)
    public Optional<Quiz> findQuizByCode(String code) {
        return quizRepository.findByJoinCode(code);
    }

    // ✅ Get all published quizzes (alias)
    public List<Quiz> getAllPublishedQuizzes() {
        return quizRepository.findByPublishedTrue();
    }

    // ✅ Delete quiz
    public void deleteQuiz(Long quizId, Long teacherId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(teacherId)) {
            throw new RuntimeException("You can only delete your own quizzes");
        }

        quizRepository.delete(quiz);
    }

    // ✅ Check if user owns the quiz
    public boolean isQuizOwner(Long quizId, Long teacherId) {
        return quizRepository.findById(quizId)
                .map(quiz -> quiz.getCreatedBy().getId().equals(teacherId))
                .orElse(false);
    }

    // ✅ Generate unique join code
    private String generateUniqueJoinCode() {
        String code;
        do {
            code = String.format("%06d", random.nextInt(1000000));
        } while (quizRepository.existsByJoinCode(code));
        return code;
    }

    // ✅ Update quiz
    public Quiz updateQuiz(Long quizId, Long teacherId, String title, String description, Integer timePerQuestion) {
        Quiz quiz = findByIdOrThrow(quizId);

        if (!quiz.getCreatedBy().getId().equals(teacherId)) {
            throw new RuntimeException("You can only update your own quizzes");
        }

        if (title != null && !title.trim().isEmpty()) {
            quiz.setTitle(title);
        }
        if (description != null) {
            quiz.setDescription(description);
        }
        if (timePerQuestion != null && timePerQuestion > 0) {
            quiz.setTimePerQuestion(timePerQuestion);
        }

        return quizRepository.save(quiz);
    }

    // ✅ Get all quizzes (for admin purposes)
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
}