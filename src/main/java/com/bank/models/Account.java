package com.bank.models;

import com.bank.enums.AccountType;

import java.math.BigDecimal;

public class Account {
    private Long id;
    private String RIB;
    private Long customerId;
    private BigDecimal balance;
    private AccountType accountType;
    private String Devise;
    private String status;
    private String createdAt;

    // Default constructor
    public Account() {
    }

    // Constructor with parameters
    public Account(String RIB, Long customerId, BigDecimal balance, AccountType accountType, String currency, String status, String createdAt) {
        this.RIB = RIB;
        this.customerId = customerId;
        this.balance = balance;
        this.accountType = accountType;
        this.Devise = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getAccountRib() {
        return RIB;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setAccountRib(String accountId) {
        this.RIB = accountId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public AccountType setAccountType(AccountType accountType) {
        this.accountType = accountType;
        return accountType;
    }

    public String getCurrency() {
        return Devise;
    }

    public void setCurrency(String currency) {
        this.Devise = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}