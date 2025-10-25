package com.example.quizzapp.service;

import com.example.quizzapp.dto.QuizDTO;
import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserService userService;

    // Publish/Unpublish methods
    public boolean publishQuiz(Long quizId, Long teacherId) {
        Optional<Quiz> quizOpt = quizRepository.findById(quizId);

        if (quizOpt.isPresent() && quizOpt.get().getTeacher().getId().equals(teacherId)) {
            Quiz quiz = quizOpt.get();
            if (!quiz.getQuestions().isEmpty()) {
                quiz.setPublished(true);
                quizRepository.save(quiz);
                return true;
            }
        }
        return false;
    }

    public boolean unpublishQuiz(Long quizId, Long teacherId) {
        Optional<Quiz> quizOpt = quizRepository.findById(quizId);

        if (quizOpt.isPresent() && quizOpt.get().getTeacher().getId().equals(teacherId)) {
            Quiz quiz = quizOpt.get();
            quiz.setPublished(false);
            quizRepository.save(quiz);
            return true;
        }
        return false;
    }

    // Get quizzes methods
    public List<Quiz> getAvailableQuizzes() {
        return quizRepository.findByPublishedTrue();
    }

    public List<Quiz> getAllActiveQuizzes() {
        return quizRepository.findByPublishedTrue();
    }

    public List<QuizDTO> getPublishedQuizzes() {
        return quizRepository.findByPublishedTrueOrderByPublishedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Quiz> getQuizzesByTeacher(Long teacherId) {
        User teacher = userService.getUserById(teacherId);
        return quizRepository.findByTeacher(teacher);
    }

    public List<QuizDTO> getQuizzesByTeacherAsDTO(Long teacherId) {
        User teacher = userService.getUserById(teacherId);
        return quizRepository.findByTeacher(teacher)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QuizDTO> getPublishedQuizzesByTeacher(Long teacherId) {
        User teacher = userService.getUserById(teacherId);
        return quizRepository.findByTeacherAndPublished(teacher, true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<QuizDTO> getPublishedQuiz(Long quizId) {
        return quizRepository.findByIdAndPublishedTrue(quizId)
                .map(this::convertToDTO);
    }

    // Stats methods for DashboardController
    public long getQuizzesAttemptedCount(Long studentId) {
        // TODO: Implement actual logic using QuizAttemptRepository
        return 0L;
    }

    public double getAverageScore(Long studentId) {
        // TODO: Implement actual logic using QuizAttemptRepository
        return 0.0;
    }

    public long getTotalScore(Long studentId) {
        // TODO: Implement actual logic using QuizAttemptRepository
        return 0L;
    }

    public long getQuizzesCreatedCount(Long teacherId) {
        User teacher = userService.getUserById(teacherId);
        return quizRepository.countByTeacher(teacher);
    }

    public long getTotalParticipants(Long teacherId) {
        // TODO: Implement actual logic - count unique students who attempted teacher's quizzes
        return 0L;
    }

    // Utility methods
    public boolean isQuizOwner(Long quizId, Long teacherId) {
        Optional<Quiz> quiz = quizRepository.findById(quizId);
        return quiz.isPresent() && quiz.get().getTeacher().getId().equals(teacherId);
    }

    public Optional<Quiz> getQuizById(Long quizId) {
        return quizRepository.findById(quizId);
    }

    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long quizId) {
        quizRepository.deleteById(quizId);
    }

    // Conversion method
    private QuizDTO convertToDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setPublished(quiz.isPublished());
        dto.setPublishedAt(quiz.getPublishedAt());
        dto.setCreatedAt(quiz.getCreatedAt());
        dto.setTeacherId(quiz.getTeacher().getId());
        dto.setTeacherName(quiz.getTeacher().getUsername());
        dto.setQuestionCount(quiz.getQuestions().size());
        return dto;
    }
}