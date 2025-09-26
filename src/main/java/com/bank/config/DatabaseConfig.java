package com.bank.config;//package com.bank.config;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    public static Connection DbConnection() {
        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String username = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected successfully to PostgreSQL!");
            return connection;
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return null;
        }
    }
}
