package com.inventory.app.util;

import com.inventory.app.model.User;

/**
 * Singleton session manager that tracks the currently logged-in user.
 * Provides methods for login, logout, and role-based access checks.
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {
        // Private constructor for singleton
    }

    /**
     * Returns the singleton instance of SessionManager.
     *
     * @return the SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Logs in a user by storing their reference in the session.
     *
     * @param user the User to log in
     */
    public void login(User user) {
        this.currentUser = user;
    }

    /**
     * Logs out the current user by clearing the stored reference.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return the current User, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks if the current user has the admin role.
     *
     * @return true if the current user's role is "admin", false otherwise
     */
    public boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }
}
