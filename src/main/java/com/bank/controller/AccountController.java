package com.bank.controller;

import com.bank.models.Account;
import com.bank.models.Client;
import com.bank.enums.AccountType;
import com.bank.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

public class AccountController {
    AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    public void deposit(String rib, BigDecimal amount) {

        accountService.deposit(rib, amount);

    }

    public void withdraw(String rib, BigDecimal amount) {
        accountService.withdraw(rib, amount);
    }
}
