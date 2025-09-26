package com.bank.service;

import com.bank.models.User;
import com.bank.repository.interfaces.AuthRepository;

public class AuthService {
    private AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository  = authRepository;
    }

    public User login(String email, String password) {
        User user = authRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }
}
