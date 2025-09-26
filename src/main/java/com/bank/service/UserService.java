package com.bank.service;

import com.bank.models.User;
import com.bank.enums.Role;
import com.bank.repository.Impl.UserRepositoryImpl;

import java.util.List;

public class UserService {
    private final UserRepositoryImpl userRepository;

    public UserService(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email, String phoneNumber, String address, String password, String roleInput) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        user.setPassword(password);

        try {
            user.setRole(Role.valueOf(roleInput.toUpperCase()));
            userRepository.CreateUser(user);
            return user;
        } catch (IllegalArgumentException e) {
            return null;
        }

    }
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
