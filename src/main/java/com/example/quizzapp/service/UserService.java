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
        user.setCurrentQuizCode(null); // Initialize as null

        return userRepository.save(user);
    }

    // ✅ Find user by username (returns Optional)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ✅ Find user by username (throws error if not found)
    public User findByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    // ✅ Find user by ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // ✅ Find user by ID (throws error if not found)
    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
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
        User student = findByIdOrThrow(studentId);
        if (student.getRole() != UserRole.STUDENT) {
            throw new RuntimeException("User is not a student");
        }
        student.setCurrentQuizCode(quizCode);
        return userRepository.save(student);
    }

    // ✅ Clear student's current quiz code
    public User clearUserCurrentQuiz(Long studentId) {
        User student = findByIdOrThrow(studentId);
        student.setCurrentQuizCode(null);
        return userRepository.save(student);
    }

    // ✅ Check if user is a teacher
    public boolean isTeacher(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == UserRole.TEACHER)
                .orElse(false);
    }

    // ✅ Check if user is a student
    public boolean isStudent(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == UserRole.STUDENT)
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

    // ✅ Delete user by ID
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // ✅ Get all users
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // ✅ Find by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}