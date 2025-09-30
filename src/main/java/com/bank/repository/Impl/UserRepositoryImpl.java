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
        String query = "INSERT INTO users (name, email, phoneNumber, address, password, role) VALUES (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(query);
        )
             {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getRole().name());

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
                user.setCustomerId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
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
