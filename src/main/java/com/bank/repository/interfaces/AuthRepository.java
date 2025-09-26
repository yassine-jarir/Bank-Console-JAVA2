package com.bank.repository.interfaces;

import com.bank.models.User;

public interface AuthRepository {

    User findByEmail(String email);
}
