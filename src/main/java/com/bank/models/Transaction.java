package com.bank.models;

import com.bank.enums.AccountType;
import com.bank.enums.TransactionStatus;
import com.bank.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Long transactionId;
    private AccountType sourceAccountType;
    private AccountType targetAccountType;
    private BigDecimal amount;
    private String sourceAccountId;
    private String targetAccountId;
    private TransactionType transactionType;
    private TransactionStatus status;
    private LocalDateTime transactionDate;

    private String sourceClientEmail;
    private String targetClientEmail;
    private Account sourceAccount;
    private Account targetAccount;

    // Fields for displaying account RIB information in admin views
    private String sourceAccountRib;
    private String targetAccountRib;

    // Constructor
//    public Transaction(String transactionId, AccountType sourceAccountType,AccountType targetAccountType,  BigDecimal amount, String sourceAccountId, String targetAccountId, TransactionStatus status, LocalDateTime transactionDate) {
//        this.transactionId = transactionId;
//        this.sourceAccountType = sourceAccountType;
//        this.targetAccountType = targetAccountType;
//        this.amount = amount;
//        this.sourceAccountId = sourceAccountId;
//        this.targetAccountId = targetAccountId;
//        this.status = status;
//        this.transactionDate = transactionDate;
//    }

    // Getters and setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
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
    public TransactionType getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    // New getters and setters for client emails and accounts
    public String getSourceClientEmail() {
        return sourceClientEmail;
    }

    public void setSourceClientEmail(String sourceClientEmail) {
        this.sourceClientEmail = sourceClientEmail;
    }

    public String getTargetClientEmail() {
        return targetClientEmail;
    }

    public void setTargetClientEmail(String targetClientEmail) {
        this.targetClientEmail = targetClientEmail;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public Account getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(Account targetAccount) {
        this.targetAccount = targetAccount;
    }

    public String getSourceAccountRib() {
        return sourceAccountRib;
    }

    public void setSourceAccountRib(String sourceAccountRib) {
        this.sourceAccountRib = sourceAccountRib;
    }

    public String getTargetAccountRib() {
        return targetAccountRib;
    }

    public void setTargetAccountRib(String targetAccountRib) {
        this.targetAccountRib = targetAccountRib;
    }
}
