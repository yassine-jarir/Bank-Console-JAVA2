package com.bank.repository.Impl;

import com.bank.config.DatabaseConfig;
import com.bank.models.User;
import com.bank.enums.Role;
import com.bank.repository.interfaces.AuthRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthRepositoryImpl implements AuthRepository {
    private Connection conn;
    public AuthRepositoryImpl() {
        this.conn = DatabaseConfig.getInstance().getConnection();
    }

    @Override
    public User findByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setCustomerId(rs.getLong("user_id"));
                // Concatenate first_name and last_name
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String fullName = firstName + (lastName != null && !lastName.isEmpty() ? " " + lastName : "");
                user.setName(fullName);
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhoneNumber(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setRole(Role.valueOf(rs.getString("role")));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
