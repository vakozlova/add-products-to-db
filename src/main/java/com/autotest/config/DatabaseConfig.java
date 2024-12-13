package com.autotest.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConfig {
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());
    private static final String DB_URL = "jdbc:h2:tcp://localhost:9092/mem:testdb";
    private static final String USER = "user";
    private static final String PASSWORD = "pass";

    /**
     * Метод для получения соединения с базой данных.
     *
     * @return объект Connection, представляющий соединение с БД
     * @throws RuntimeException, если не удается подключиться к базе данных.
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection failed", e);
            throw new RuntimeException("Database connection failed", e);
        }
    }
}

