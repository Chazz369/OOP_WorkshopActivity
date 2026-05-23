package com.inventory.app.config;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Lazy singleton configuration loader that reads environment variables
 * from a {@code .env} file located at the project root directory.
 * <p>
 * Uses the {@code dotenv-java} library to parse the file and exposes
 * typed accessor methods for each configuration key.
 */
public final class EnvConfig {

    // Volatile for safe double-checked locking
    private static volatile EnvConfig instance;

    private final Dotenv dotenv;

    private EnvConfig() {
        this.dotenv = Dotenv.configure()
                .directory("./")    // project root
                .ignoreIfMissing()  // don't crash if .env is absent (fallback to system env)
                .load();
    }

    /**
     * Returns the singleton instance, creating it lazily on first access.
     *
     * @return the shared {@code EnvConfig} instance
     */
    public static EnvConfig getInstance() {
        if (instance == null) {
            synchronized (EnvConfig.class) {
                if (instance == null) {
                    instance = new EnvConfig();
                }
            }
        }
        return instance;
    }

    // ------------------------------------------------------------------
    // Database configuration
    // ------------------------------------------------------------------

    /**
     * JDBC connection URL for the PostgreSQL / Supabase database.
     */
    public String getDbUrl() {
        return dotenv.get("DB_URL");
    }

    /**
     * Database user name.
     */
    public String getDbUser() {
        return dotenv.get("DB_USER");
    }

    /**
     * Database password.
     */
    public String getDbPassword() {
        return dotenv.get("DB_PASSWORD");
    }

    // ------------------------------------------------------------------
    // Application-level secrets
    // ------------------------------------------------------------------

    /**
     * A secret key used for application-level signing or token generation.
     */
    public String getAppSecretKey() {
        return dotenv.get("APP_SECRET_KEY");
    }
}
