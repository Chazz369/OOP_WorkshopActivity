package com.inventory.app.service;

import com.inventory.app.dao.UserDAO;
import com.inventory.app.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

/**
 * Service responsible for user authentication and password hashing.
 * Uses BCrypt for secure password handling.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Authenticates a user by username and plain-text password.
     * Looks up the user via UserDAO and verifies the password using BCrypt.
     *
     * @param username      the username to authenticate
     * @param plainPassword the plain-text password to verify
     * @return an Optional containing the User if authentication succeeds, or empty if it fails
     */
    public Optional<User> authenticate(String username, String plainPassword) {
        Optional<User> optionalUser = userDAO.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    /**
     * Hashes a plain-text password using BCrypt with a cost factor of 12.
     *
     * @param plainPassword the plain-text password to hash
     * @return the BCrypt hashed password string
     */
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }
}
