package com.inventory.app.controller;

import com.inventory.app.dao.UserDAO;
import com.inventory.app.model.User;
import com.inventory.app.service.AuthService;
import com.inventory.app.util.SceneManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Controller for the sign-up screen.
 * Handles new user registration with BCrypt password hashing.
 * The plain-text password entered here is hashed using BCrypt before
 * being stored in the database. On login, the entered password is
 * compared against the stored hash using BCrypt.checkpw().
 */
public class SignUpController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Button signUpButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final UserDAO userDAO = new UserDAO();
    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Allow Enter key to trigger sign up from confirm password field
        confirmPasswordField.setOnAction(event -> handleSignUp());
    }

    @FXML
    private void handleSignUp() {
        hideError();
        hideSuccess();

        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate inputs
        if (fullName.isEmpty()) {
            showError("Please enter your full name.");
            return;
        }
        if (username.isEmpty()) {
            showError("Please enter a username.");
            return;
        }
        if (username.length() < 3) {
            showError("Username must be at least 3 characters.");
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter a password.");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        // Show loading state
        setLoadingState(true);

        Task<Boolean> signUpTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Check if username already exists
                if (userDAO.findByUsername(username).isPresent()) {
                    return false;
                }

                // Hash password and create user
                User newUser = new User();
                newUser.setId(UUID.randomUUID());
                newUser.setUsername(username);
                newUser.setPasswordHash(authService.hashPassword(password));
                newUser.setFullName(fullName);
                newUser.setRole("staff");
                newUser.setCreatedAt(LocalDateTime.now());

                userDAO.insertUser(newUser);
                return true;
            }
        };

        signUpTask.setOnSucceeded(event -> {
            setLoadingState(false);
            if (signUpTask.getValue()) {
                showSuccess("Account created! You can now sign in.");
                clearFields();
            } else {
                showError("Username already taken. Please choose another.");
            }
        });

        signUpTask.setOnFailed(event -> {
            setLoadingState(false);
            showError("An error occurred. Please try again.");
        });

        Thread thread = new Thread(signUpTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleBackToLogin() {
        SceneManager.getInstance().switchScene("/com/inventory/app/fxml/login.fxml",
                "Inventory Management System - Login");
    }

    private void setLoadingState(boolean loading) {
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
        signUpButton.setDisable(loading);
    }

    private void showError(String message) {
        hideSuccess();
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void showSuccess(String message) {
        hideError();
        successLabel.setText(message);
        successLabel.setVisible(true);
        successLabel.setManaged(true);
    }

    private void hideSuccess() {
        successLabel.setVisible(false);
        successLabel.setManaged(false);
    }

    private void clearFields() {
        fullNameField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}
