package com.example.quizzapp.service;


import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.QuizParticipation;
import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.QuizParticipationRepository;
import com.example.quizzapp.repository.QuizRepository;
import com.example.quizzapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizParticipationService {

    @Autowired
    private QuizParticipationRepository participationRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    public QuizParticipation submitQuizResults(Long quizId, Long studentId, Integer score,
                                               Integer correctAnswers, Integer timeTaken) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Check if student already participated
        Optional<QuizParticipation> existingParticipation =
                participationRepository.findByQuizAndStudent(quiz, student);

        if (existingParticipation.isPresent()) {
            // Update existing participation
            QuizParticipation participation = existingParticipation.get();
            participation.setScore(score);
            participation.setCorrectAnswers(correctAnswers);
            participation.setTimeTaken(timeTaken);
            return participationRepository.save(participation);
        } else {
            // Create new participation
            QuizParticipation participation = new QuizParticipation(quiz, student);
            participation.setScore(score);
            participation.setCorrectAnswers(correctAnswers);
            participation.setTimeTaken(timeTaken);
            return participationRepository.save(participation);
        }
    }

    public List<QuizParticipation> getLeaderboardForQuiz(Long quizId) {
        return participationRepository.findLeaderboardByQuiz(quizId);
    }

    public List<QuizParticipation> getStudentResults(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return participationRepository.findByStudent(student);
    }

    public List<QuizParticipation> getQuizParticipants(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        return participationRepository.findByQuiz(quiz);
    }

    public Optional<QuizParticipation> getStudentQuizResult(Long quizId, Long studentId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return participationRepository.findByQuizAndStudent(quiz, student);
    }

    public Integer getQuizParticipationCount(Long quizId) {
        return participationRepository.countParticipationsByQuiz(quizId);
    }
}