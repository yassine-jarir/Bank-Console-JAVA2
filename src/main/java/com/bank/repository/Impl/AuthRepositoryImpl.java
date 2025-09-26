package com.bank.repository.Impl;

import com.bank.config.DatabaseConfig;
import com.bank.models.User;
import com.bank.enums.Role;
import com.bank.repository.interfaces.AuthRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthRepositoryImpl implements AuthRepository {

    @Override
    public User findByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConfig.DbConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setCustomerId(String.valueOf(rs.getInt("user_id")));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
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
