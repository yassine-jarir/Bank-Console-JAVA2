package com.bank.service;

import com.bank.models.Account;
import com.bank.repository.interfaces.AccountRepository;

public class AccountService {
    // Account service methods
    AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }
    
    public Account connexion(String RIB , String password){
        return null;
    }

}
