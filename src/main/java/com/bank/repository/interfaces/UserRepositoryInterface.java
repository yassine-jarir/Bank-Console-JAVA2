package com.bank.repository.interfaces;

import com.bank.models.User;

import java.util.List;

public interface UserRepositoryInterface {
    User CreateUser(User user);
    List<User> getAllUsers();
}
