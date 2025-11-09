package com.example.quizzapp.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for input validation and sanitization
 */
@Component
public class ValidationUtil {

    /**
     * Sanitize string input by removing potentially harmful characters
     * @param input Input string
     * @return Sanitized string
     */
    public static String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        // Remove HTML tags and script tags
        return input.replaceAll("<[^>]*>", "").trim();
    }

    /**
     * Validate email format
     * @param email Email to validate
     * @return true if email is valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validate username format (alphanumeric and underscore only)
     * @param username Username to validate
     * @return true if username is valid
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        String usernameRegex = "^[a-zA-Z0-9_]{3,50}$";
        return username.matches(usernameRegex);
    }

    /**
     * Validate quiz code format (alphanumeric, 6 characters)
     * @param code Code to validate
     * @return true if code is valid
     */
    public static boolean isValidQuizCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        String codeRegex = "^[A-Z0-9]{6}$";
        return code.matches(codeRegex);
    }

    /**
     * Validate that a number is within a range
     * @param value Value to check
     * @param min Minimum value
     * @param max Maximum value
     * @return true if value is within range
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Truncate string to maximum length
     * @param input Input string
     * @param maxLength Maximum length
     * @return Truncated string
     */
    public static String truncate(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        if (input.length() <= maxLength) {
            return input;
        }
        return input.substring(0, maxLength);
    }
}
