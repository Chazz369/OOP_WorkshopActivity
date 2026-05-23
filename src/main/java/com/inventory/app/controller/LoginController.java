package com.inventory.app.controller;

import com.inventory.app.model.User;
import com.inventory.app.service.AuthService;
import com.inventory.app.util.SceneManager;
import com.inventory.app.util.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * Controller for the login screen.
 * Handles user authentication with background threading and remember-me functionality.
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Button signInButton;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private static final String PREF_KEY_USERNAME = "remembered_username";
    private static final String PREF_KEY_REMEMBER = "remember_me";
    private final Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Check for saved username from Remember Me
        boolean rememberMe = prefs.getBoolean(PREF_KEY_REMEMBER, false);
        if (rememberMe) {
            String savedUsername = prefs.get(PREF_KEY_USERNAME, "");
            if (!savedUsername.isEmpty()) {
                usernameField.setText(savedUsername);
                rememberMeCheckbox.setSelected(true);
                Platform.runLater(() -> passwordField.requestFocus());
            }
        }

        // Allow Enter key to trigger sign in from password field
        passwordField.setOnAction(event -> handleSignIn());
        usernameField.setOnAction(event -> passwordField.requestFocus());
    }

    @FXML
    private void handleSignIn() {
        hideError();

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        setLoadingState(true);

        Task<Optional<User>> authTask = new Task<>() {
            @Override
            protected Optional<User> call() throws Exception {
                return authService.authenticate(username, password);
            }
        };

        authTask.setOnSucceeded(event -> {
            Optional<User> result = authTask.getValue();
            if (result.isPresent()) {
                User user = result.get();

                if (rememberMeCheckbox.isSelected()) {
                    prefs.put(PREF_KEY_USERNAME, username);
                    prefs.putBoolean(PREF_KEY_REMEMBER, true);
                } else {
                    prefs.remove(PREF_KEY_USERNAME);
                    prefs.putBoolean(PREF_KEY_REMEMBER, false);
                }

                SessionManager.getInstance().login(user);
                SceneManager.getInstance().switchScene("/com/inventory/app/fxml/inventory.fxml",
                        "Inventory Management System");
            } else {
                showError("Invalid username or password.");
                setLoadingState(false);
            }
        });

        authTask.setOnFailed(event -> {
            showError("An error occurred. Please try again.");
            setLoadingState(false);
        });

        Thread thread = new Thread(authTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleSignUp() {
        SceneManager.getInstance().switchScene("/com/inventory/app/fxml/signup.fxml",
                "Inventory Management System - Sign Up");
    }

    private void setLoadingState(boolean loading) {
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
        signInButton.setDisable(loading);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
