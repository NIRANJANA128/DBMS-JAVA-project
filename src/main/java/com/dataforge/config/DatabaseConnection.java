package com.dataforge.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connection configuration and connectivity.
 * Reads configuration from environment variables (DB_URL, DB_USER, DB_PASSWORD)
 * with reliable default fallbacks for local MySQL development.
 */
public class DatabaseConnection {

    // Default configuration constants
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/dataforge?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "root";

    // Driver class name
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL JDBC Driver (" + DRIVER_CLASS + ") not found in classpath.");
            System.err.println("Please make sure mysql-connector-j jar is present in the classpath or lib folder.");
        }
    }

    /**
     * Resolves the JDBC URL from environment variable DB_URL or fallback.
     */
    public static String getDbUrl() {
        String envUrl = System.getenv("DB_URL");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            return envUrl.trim();
        }
        return DEFAULT_URL;
    }

    /**
     * Resolves the database username from environment variable DB_USER or fallback.
     */
    public static String getDbUser() {
        String envUser = System.getenv("DB_USER");
        if (envUser != null && !envUser.trim().isEmpty()) {
            return envUser.trim();
        }
        return DEFAULT_USER;
    }

    /**
     * Resolves the database password from environment variable DB_PASSWORD or fallback.
     */
    public static String getDbPassword() {
        String envPass = System.getenv("DB_PASSWORD");
        if (envPass != null) {
            return envPass;
        }
        return DEFAULT_PASSWORD;
    }

    /**
     * Establishes and returns a new JDBC Connection.
     * Callers must close this connection (preferably using try-with-resources).
     *
     * @return active java.sql.Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
    }

    /**
     * Validates database connectivity and prints a friendly diagnostic message.
     *
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[DATABASE CONNECTION ERROR]: " + e.getMessage());
            System.err.println("Target URL : " + getDbUrl());
            System.err.println("Target User: " + getDbUser());
            System.err.println("Hint       : Ensure MySQL Server is running and credentials in environment variables (DB_USER, DB_PASSWORD) or defaults are correct.");
            return false;
        }
    }
}
