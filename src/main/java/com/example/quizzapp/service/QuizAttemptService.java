package com.example.quizzapp.service;

import com.example.quizzapp.dto.LeaderboardEntryDTO;
import com.example.quizzapp.model.*;
import com.example.quizzapp.repository.QuizAttemptRepository;
import com.example.quizzapp.repository.QuizRepository;
import com.example.quizzapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizAttemptService {
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    public QuizAttempt startQuizAttempt(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user already attempted this quiz
        Optional<QuizAttempt> existingAttempt = quizAttemptRepository.findByUserIdAndQuizId(userId, quizId);
        if (existingAttempt.isPresent()) {
            throw new RuntimeException("You have already attempted this quiz");
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setStartedAt(LocalDateTime.now());

        return quizAttemptRepository.save(attempt);
    }

    public QuizAttempt submitQuizAttempt(Long attemptId, Map<Long, Long> answers) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        int totalScore = 0;

        for (Map.Entry<Long, Long> entry : answers.entrySet()) {
            Long questionId = entry.getKey();
            Long selectedOptionId = entry.getValue();

            // Find question and selected option
            Question question = attempt.getQuiz().getQuestions().stream()
                    .filter(q -> q.getId().equals(questionId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            Option selectedOption = question.getOptions().stream()
                    .filter(o -> o.getId().equals(selectedOptionId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Option not found"));

            // Create user answer
            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setQuizAttempt(attempt);
            userAnswer.setQuestion(question);
            userAnswer.setSelectedOption(selectedOption);
            userAnswer.setIsCorrect(selectedOption.getIsCorrect());

            if (selectedOption.getIsCorrect()) {
                userAnswer.setPointsEarned(question.getPoints());
                totalScore += question.getPoints();
            }

            attempt.getUserAnswers().add(userAnswer);
        }

        attempt.setScore(totalScore);
        attempt.setCompletedAt(LocalDateTime.now());

        return quizAttemptRepository.save(attempt);
    }

    public List<QuizAttempt> getQuizAttemptsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return quizAttemptRepository.findByUser(user);
    }

    public List<LeaderboardEntryDTO> getQuizLeaderboard(Long quizId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findLeaderboardByQuizId(quizId);

        return attempts.stream()
                .map(attempt -> new LeaderboardEntryDTO(
                        attempt.getUser().getUsername(),
                        attempt.getScore(),
                        attempts.indexOf(attempt) + 1
                ))
                .collect(Collectors.toList());
    }

    public List<LeaderboardEntryDTO> getGlobalLeaderboard() {
        List<Object[]> results = quizAttemptRepository.findGlobalLeaderboard();

        return results.stream()
                .map(result -> new LeaderboardEntryDTO(
                        (String) result[0],
                        ((Long) result[1]).intValue(),
                        results.indexOf(result) + 1
                ))
                .collect(Collectors.toList());
    }
}