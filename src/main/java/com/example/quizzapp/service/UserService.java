package com.example.quizzapp.service;

import com.example.quizzapp.model.User;
import com.example.quizzapp.model.UserRole;
import com.example.quizzapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Add these methods for password reset functionality
    public void sendPasswordResetEmail(String email) {
        // For now, just check if email exists
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("Email not found");
        }
        // In a real application, you would:
        // 1. Generate a reset token
        // 2. Save it to the database with expiration
        // 3. Send email with reset link
        throw new RuntimeException("Password reset email functionality not implemented yet");
    }

    public boolean validateResetToken(String token) {
        // For now, return false
        // In a real application, you would validate the token against the database
        return false;
    }

    public boolean resetPassword(String token, String newPassword) {
        // For now, return false
        // In a real application, you would:
        // 1. Validate the token
        // 2. Find the user associated with the token
        // 3. Update the password
        // 4. Invalidate the token
        return false;
    }

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

    public User registerUser(com.example.quizzapp.dto.SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(signUpRequest.getRole());

        return userRepository.save(user);
    }

    public Long getUserIdByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::getId).orElse(null);
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }
    // Add this method to your existing UserService class
    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}