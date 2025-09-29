package com.bank.controller;

import com.bank.config.DatabaseConfig;
import com.bank.models.User;
import com.bank.service.UserService;

import java.util.List;

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
    public List<User>  GetAllUsers() {
        List<User> users =  userService.getAllUsers();
        return users;
    }
    public void DeleteUser(int index) {

        try{
            userService.DeleteUser(index);
            System.out.println("User deleted successfully.");
        }catch (Exception e){
            System.out.println("Error: Invalid index provided.");
            return;
        }
    }
}



