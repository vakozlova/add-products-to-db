package com.autotest.utils;

import java.sql.*;

public class Statement {
    private Connection connection;

    public Statement(Connection connection) {
        this.connection = connection;
    }

    // Метод для выполнения SQL-запроса SELECT и возвращения результата
    public ResultSet executeQuery(String query) throws SQLException {
        java.sql.Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    // Метод для выполнения SQL-запроса INSERT, UPDATE, DELETE
    public void executeUpdate(String query) throws SQLException {
        java.sql.Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }
}
