package com.bank.controller;

import com.bank.models.User;
import com.bank.service.UserService;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void CreateUser(String name, String email, String phoneNumber, String address, String password, String role) {
        User user = userService.createUser(name, email, phoneNumber, address, password, role);

        if (user != null) {
            System.out.println("User created successfully: " + user.getName() + ", Role: " + user.getRole());
        } else {
            System.out.println("Error: Invalid role provided.");

        }
    }
    public void GetAllUsers() {
        userService.getAllUsers().forEach(user -> {
            System.out.println("User: " + user.getName() + ", Email: " + user.getEmail() + ", Role: " + user.getRole());
        });
    }
}



