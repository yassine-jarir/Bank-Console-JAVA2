package com.bank.models;

import com.bank.enums.AccountType;
import com.bank.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private AccountType sourceAccountType;
    private AccountType targetAccountType;
    private BigDecimal amount;
    private String sourceAccountId;
    private String targetAccountId;
    private TransactionStatus status;
    private LocalDateTime transactionDate;

    // Constructor
    public Transaction(String transactionId, AccountType sourceAccountType,AccountType targetAccountType,  BigDecimal amount, String sourceAccountId, String targetAccountId, TransactionStatus status, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.sourceAccountType = sourceAccountType;
        this.targetAccountType = targetAccountType;
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.status = status;
        this.transactionDate = transactionDate;
    }

    // Getters and setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public AccountType getSourceAccountType() {
        return sourceAccountType;
    }
    public void setSourceAccountType(AccountType sourceAccountType) {
        this.sourceAccountType = sourceAccountType;
    }
    public AccountType getTargetAccountType() {
        return targetAccountType;
    }
    public void setTargetAccountType(AccountType targetAccountType) {
        this.targetAccountType = targetAccountType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(String sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public String getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(String targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
