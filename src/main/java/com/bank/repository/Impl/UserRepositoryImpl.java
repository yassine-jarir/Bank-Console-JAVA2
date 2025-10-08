package com.bank.repository.Impl;

import com.bank.config.DatabaseConfig;
import com.bank.models.User;
import com.bank.repository.interfaces.UserRepositoryInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class UserRepositoryImpl implements UserRepositoryInterface {
    private Connection conn;

    public  UserRepositoryImpl () {
         this.conn = DatabaseConfig.getInstance().getConnection();
    }

     public User CreateUser(User user) {
        String query = "INSERT INTO users (first_name, last_name, email, phone, address, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(query);
        )
             {
            // Split the name into first and last name
            String[] nameParts = user.getName().trim().split("\\s+", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : ""; // Use empty string if no last name

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getPassword());
            stmt.setString(7, user.getRole().name());

            stmt.executeUpdate();
            return user;
        } catch (Exception e) {
            e.printStackTrace();
           return null;
        }
    }
    public List<User> getAllUsers() {
        String query = "SELECT * FROM users";
        java.util.List<User> users = new java.util.ArrayList<>();
        try( PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery();)
            {
                while (rs.next()) {
                User user = new User();
                user.setCustomerId(rs.getLong("user_id"));
                // Concatenate first_name and last_name
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String fullName = firstName + (lastName != null && !lastName.isEmpty() ? " " + lastName : "");
                user.setName(fullName);
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setPassword(rs.getString("password"));
                user.setRole(com.bank.enums.Role.valueOf(rs.getString("role")));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public void DeleteUser(int index) {
         String query = "DELETE FROM users WHERE user_id  = ? ";
         try (
                 PreparedStatement stmt = conn.prepareStatement(query);

                 ) {
             stmt.setInt(1, index);
             stmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
         }

    }
}
