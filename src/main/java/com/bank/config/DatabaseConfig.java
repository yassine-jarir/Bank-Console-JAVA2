package com.bank.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseConfig {

    public static void main() {
        String url = "jdbc:postgresql://localhost:5432/bankSystem";
        String username = "postgres";
        String password = "123";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to PostgreSQL!");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT version();");

            while (resultSet.next()) {
                System.out.println("PostgreSQL Version: " + resultSet.getString(1));
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
