package com.bank.models;

import java.math.BigDecimal;

public class Account {
    private String RIB;
    private int customerId;
    private BigDecimal balance;
    private String accountType;
    private String Devise;
    private String status;
    private String createdAt;

    // Constructor
    public Account(String accountId, int customerId, BigDecimal balance, String accountType, String currency, String status, String createdAt) {
        this.RIB = accountId;
        this.customerId = customerId;
        this.balance = balance;
        this.accountType = accountType;
        this.Devise = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getAccountId() {
        return RIB;
    }

    public void setAccountId(String accountId) {
        this.RIB = accountId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
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
