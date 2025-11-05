package com.example.quizzapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.example.quizzapp.model.UserRole;

public class SignupRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    @Size(min = 6, max = 40, message = "Confirm password must be between 6 and 40 characters")
    private String confirmPassword;

    @NotNull(message = "Role is required")
    private UserRole role;

    // Default constructor
    public SignupRequest() {}

    // Constructor without confirmPassword for flexibility
    public SignupRequest(String username, String email, String password, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = password; // Set confirmPassword to same value
        this.role = role;
    }

    // Full constructor
    public SignupRequest(String username, String email, String password, String confirmPassword, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.role = role;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    // Utility method to check if passwords match
    public boolean doPasswordsMatch() {
        return password != null && password.equals(confirmPassword);
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "SignupRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", confirmPassword='[PROTECTED]'" +
                ", role=" + role +
                '}';
    }
}