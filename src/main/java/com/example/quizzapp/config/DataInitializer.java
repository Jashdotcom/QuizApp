package com.example.quizzapp.config;

import com.example.quizzapp.model.User;
import com.example.quizzapp.model.UserRole;
import com.example.quizzapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for initializing database with default data
 * Creates default test users for development and testing
 */
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            logger.info("Initializing default data...");

            // Create default teacher account if not exists
            if (!userRepository.existsByUsername("teacher")) {
                User teacher = new User();
                teacher.setUsername("teacher");
                teacher.setEmail("teacher@quizapp.com");
                teacher.setPassword(passwordEncoder.encode("teacher123"));
                teacher.setRole(UserRole.TEACHER);
                userRepository.save(teacher);
                logger.info("Created default teacher account: teacher/teacher123");
            }

            // Create default student account if not exists
            if (!userRepository.existsByUsername("student")) {
                User student = new User();
                student.setUsername("student");
                student.setEmail("student@quizapp.com");
                student.setPassword(passwordEncoder.encode("student123"));
                student.setRole(UserRole.STUDENT);
                userRepository.save(student);
                logger.info("Created default student account: student/student123");
            }

            // Create default admin account if not exists
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@quizapp.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(UserRole.TEACHER); // Admin has teacher role for now
                userRepository.save(admin);
                logger.info("Created default admin account: admin/admin123");
            }

            logger.info("Data initialization completed.");
            logger.warn("⚠️  Default test accounts are active. Change passwords in production!");
        };
    }
}
