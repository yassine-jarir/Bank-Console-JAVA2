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
    public void internalTransfer(Client client, String sourceRib, String destRib, BigDecimal amount){
        accountService.internalTransfer(client, sourceRib, destRib, amount);
    }
    public void externalTransfer(Client client, String sourceRib, String destRib, BigDecimal amount){
        accountService.externalTransfer(client, sourceRib, destRib, amount);
    }
    public void createNewAccount(Client client){
        try {
            accountService.createNewAccount(client);
            System.out.println("Account created successfully.");
        }catch (Exception e){
            System.out.println("Failed to create account: " + e.getMessage());
        }
    }

    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    public Account createCreditAccount(Client client, BigDecimal loanAmount) {
        try {
            return accountService.createCreditAccount(client, loanAmount);
        } catch (Exception e) {
            System.out.println("Error creating credit account: " + e.getMessage());
            return null;
        }
    }

    public BigDecimal getCreditInterestRate() {
        try {
            return accountService.getCreditInterestRate();
        } catch (Exception e) {
            System.out.println("Error getting credit interest rate: " + e.getMessage());
            return new BigDecimal("5.0");
        }
    }
}
