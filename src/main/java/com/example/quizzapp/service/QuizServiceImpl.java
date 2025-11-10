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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizServiceImpl implements QuizService {
    private static final String CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RNG = new SecureRandom();

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    private String generateJoinCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CODE_ALPHABET.charAt(RNG.nextInt(CODE_ALPHABET.length())));
        }
        return sb.toString();
    }

    @Override
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    @Override
    public Quiz getQuizByCode(String code) {
        return quizRepository.findByJoinCode(code).orElse(null);
    }

    @Override
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    public Optional<Quiz> getPublishedQuizById(Long id) {
        return quizRepository.findByIdAndPublishedTrue(id);
    }

    @Override
    public Optional<Quiz> findQuizByCode(String code) {
        return quizRepository.findByJoinCode(code);
    }

    @Override
    public Optional<Quiz> findPublishedByCode(String code) {
        return quizRepository.findByJoinCodeAndPublishedTrue(code);
    }

    @Override
    public List<Quiz> getAllPublishedQuizzes() {
        return quizRepository.findByPublishedTrue();
    }

    @Override
    public List<Quiz> getPublishedQuizzesByTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return quizRepository.findByCreatedByAndPublishedTrue(teacher);
    }

    @Override
    public List<Quiz> getAvailableQuizzes() {
        return quizRepository.findByPublishedTrue();
    }

    @Override
    public Quiz publishQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setPublished(true);
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz unpublishQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setPublished(false);
        return quizRepository.save(quiz);
    }

    @Override
    public void deleteQuiz(Long quizId) {
        quizRepository.deleteById(quizId);
    }

    @Override
    public Question addQuestionToQuiz(Long quizId, String questionText, List<String> options, int correctOptionIndex) {
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
        List<Question> qs = quiz.getQuestions();
        if (qs == null) {
            qs = new ArrayList<>();
            quiz.setQuestions(qs);
        }
        qs.add(saved);
        quizRepository.save(quiz);

        return saved;
    }

    @Override
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

    @Override
    public Quiz joinQuiz(String joinCode) {
        return quizRepository.findByJoinCodeAndPublishedTrue(joinCode)
                .orElseThrow(() -> new RuntimeException("Invalid code or quiz not published"));
    }
}
