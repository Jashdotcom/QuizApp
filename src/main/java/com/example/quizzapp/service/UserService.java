package com.example.quizzapp.service;

import com.example.quizzapp.model.User;
import com.example.quizzapp.model.UserRole;
import com.example.quizzapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Register a new user
    public User registerUser(String username, String email, String password, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        return userRepository.save(user);
    }

    // ✅ Find user by username (returns Optional)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ✅ Find user by username (throws error if not found)
    public User findByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ Find user by ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // ✅ Get all students by quiz code
    public List<User> getStudentsByQuizCode(String quizCode) {
        return userRepository.findByRoleAndCurrentQuizCode(UserRole.STUDENT, quizCode);
    }

    // ✅ Get all teachers
    public List<User> getAllTeachers() {
        return userRepository.findByRole(UserRole.TEACHER);
    }

    // ✅ Get all students
    public List<User> getAllStudents() {
        return userRepository.findByRole(UserRole.STUDENT);
    }

    // ✅ Update student's current quiz code
    public User updateUserCurrentQuiz(Long studentId, String quizCode) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setCurrentQuizCode(quizCode);
        return userRepository.save(student);
    }

    // ✅ Check if user is a teacher
    public boolean isTeacher(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == UserRole.TEACHER)
                .orElse(false);
    }

    // ✅ Utility methods for validation
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // ✅ Save or update a user
    public User save(User user) {
        return userRepository.save(user);
    }

    // ✅ (Probably unused, but kept for completeness)
    public Optional<User> findByUsernameById(Long userId) {
        return userRepository.findById(userId);
    }
}
