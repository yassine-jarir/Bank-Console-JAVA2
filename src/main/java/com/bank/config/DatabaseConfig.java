package com.bank.config;//package com.bank.config;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static DatabaseConfig instance;
    private Connection connection;

    private DatabaseConfig() {
        try {
            Dotenv dotenv = Dotenv.load();

            String url = dotenv.get("DB_URL");
            String username = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");
             this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected successfully to PostgreSQL!");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    public  Connection  getConnection() {
        return connection;
    }
}

