package com.bank.controller;

import com.bank.models.User;
import com.bank.service.AuthService;
import com.bank.repository.Impl.AuthRepositoryImpl;

public class AuthController {

    private AuthService authService;

    public AuthController() {
        this.authService = new AuthService(new AuthRepositoryImpl());
    }


    public User login(String email, String password) {
      User user =  authService.login(email, password);
      if (user != null) {
          return user;
      } else {
          return null;
      }
    }
}
