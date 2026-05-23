package com.inventory.app.db;

import com.inventory.app.config.EnvConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton JDBC connection manager.
 * <p>
 * All connection parameters are sourced from {@link EnvConfig} — no
 * hardcoded credentials. Each call to {@link #getConnection()} returns
 * a <em>new</em> connection; callers are responsible for closing it
 * (preferably via try-with-resources).
 */
public final class DatabaseConnection {

    private static volatile DatabaseConnection instance;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        EnvConfig config = EnvConfig.getInstance();
        String rawUrl = config.getDbUrl();
        if (rawUrl != null && !rawUrl.contains("prepareThreshold=")) {
            if (rawUrl.contains("?")) {
                rawUrl += "&prepareThreshold=0";
            } else {
                rawUrl += "?prepareThreshold=0";
            }
        }
        this.url      = rawUrl;
        this.user     = config.getDbUser();
        this.password = config.getDbPassword();
    }

    /**
     * Returns the singleton instance.
     *
     * @return the shared {@code DatabaseConnection} manager
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Opens and returns a new JDBC {@link Connection}.
     * <p>
     * Callers <b>must</b> close the returned connection when finished,
     * ideally with a try-with-resources block.
     *
     * @return a live database connection
     * @throws SQLException if a connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Verifies that the database is reachable by opening and immediately
     * closing a connection.
     *
     * @return {@code true} if a connection was successfully established
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(5);
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
