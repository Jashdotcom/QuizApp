package com.example.quizzapp.service;

import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    // Password must be at least 8 characters with at least one letter and one number
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new user with validation
     * @param user User to register
     * @return Registered user
     * @throws IllegalArgumentException if validation fails
     */
    public User registerUser(User user) {
        logger.info("Registering new user: {}", user.getUsername());
        
        // Validate username uniqueness
        if (existsByUsername(user.getUsername())) {
            logger.warn("Username already exists: {}", user.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Validate email uniqueness
        if (existsByEmail(user.getEmail())) {
            logger.warn("Email already exists: {}", user.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Validate password strength
        if (!isPasswordStrong(user.getPassword())) {
            logger.warn("Weak password provided for user: {}", user.getUsername());
            throw new IllegalArgumentException("Password must be at least 6 characters and contain at least one letter and one number");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());
        return savedUser;
    }

    /**
     * Authenticate user with username/email and password
     * @param usernameOrEmail Username or email
     * @param password Plain text password
     * @return Optional containing user if authentication successful
     */
    public Optional<User> authenticate(String usernameOrEmail, String password) {
        logger.debug("Authenticating user: {}", usernameOrEmail);
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
        }

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            logger.info("User authenticated successfully: {}", usernameOrEmail);
            return userOpt;
        }
        logger.warn("Authentication failed for: {}", usernameOrEmail);
        return Optional.empty();
    }
    
    /**
     * Check if password meets strength requirements
     * @param password Password to check
     * @return true if password is strong enough
     */
    public boolean isPasswordStrong(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }


}
