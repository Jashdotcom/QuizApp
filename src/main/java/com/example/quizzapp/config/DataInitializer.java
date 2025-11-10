package com.example.quizzapp.config;

import com.example.quizzapp.model.Question;
import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.User;
import com.example.quizzapp.model.UserRole;
import com.example.quizzapp.repository.QuestionRepository;
import com.example.quizzapp.repository.QuizRepository;
import com.example.quizzapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create demo users
        if (userRepository.findByUsername("student").isEmpty()) {
            User student = new User();
            student.setUsername("student");
            student.setEmail("student@example.com");
            student.setRole(UserRole.STUDENT);
            student.setPassword(passwordEncoder.encode("student123"));
            userRepository.save(student);
        }

        if (userRepository.findByUsername("teacher").isEmpty()) {
            User teacher = new User();
            teacher.setUsername("teacher");
            teacher.setEmail("teacher@example.com");
            teacher.setRole(UserRole.TEACHER);
            teacher.setPassword(passwordEncoder.encode("teacher123"));
            userRepository.save(teacher);
        }

        // Create a sample quiz if none exists
        List<Quiz> published = quizRepository.findByPublishedTrue();
        if (published == null || published.isEmpty()) {
            User teacher = userRepository.findByUsername("teacher").orElse(null);
            if (teacher != null) {
                Quiz quiz = new Quiz();
                quiz.setTitle("Sample Quiz");
                quiz.setDescription("A short sample quiz to test the app functionality.");
                quiz.setJoinCode("SAMPLE1");
                quiz.setPublished(true);
                quiz.setTimePerQuestion(30);
                quiz.setCreatedBy(teacher);

                Question q1 = new Question();
                q1.setQuestionText("What is the capital of France?");
                q1.setOptions(Arrays.asList("Berlin", "Madrid", "Paris", "Rome"));
                q1.setCorrectOptionIndex(2);
                q1.setPoints(5);
                q1.setQuiz(quiz);

                Question q2 = new Question();
                q2.setQuestionText("2 + 2 = ?");
                q2.setOptions(Arrays.asList("3", "4", "5"));
                q2.setCorrectOptionIndex(1);
                q2.setPoints(3);
                q2.setQuiz(quiz);

                Question q3 = new Question();
                q3.setQuestionText("The color of the sky is:");
                q3.setOptions(Arrays.asList("Green", "Blue", "Red", "Yellow"));
                q3.setCorrectOptionIndex(1);
                q3.setPoints(2);
                q3.setQuiz(quiz);

                quiz.getQuestions().add(q1);
                quiz.getQuestions().add(q2);
                quiz.getQuestions().add(q3);

                quizRepository.save(quiz);
            }
        }
    }
}

