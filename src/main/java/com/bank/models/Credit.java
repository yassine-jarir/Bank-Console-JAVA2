package com.bank.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Credit {
    private Long creditId;
    private Long accountId;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer loanTermMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private String creditStatus; // ACTIVE, LATE, CLOSED

    // Additional fields for display
    private String clientName;
    private String clientEmail;
    private String accountRib;

    // Default constructor
    public Credit() {}

    // Constructor for creating new credit requests
    public Credit(Long accountId, BigDecimal loanAmount, BigDecimal interestRate, Integer loanTermMonths, LocalDate startDate, LocalDate endDate) {
        this.accountId = accountId;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.loanTermMonths = loanTermMonths;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creditStatus = "ACTIVE";
    }

    // Getters and setters
    public Long getCreditId() {
        return creditId;
    }

    public void setCreditId(Long creditId) {
        this.creditId = creditId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getLoanTermMonths() {
        return loanTermMonths;
    }

    public void setLoanTermMonths(Integer loanTermMonths) {
        this.loanTermMonths = loanTermMonths;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getCreditStatus() {
        return creditStatus;
    }

    public void setCreditStatus(String creditStatus) {
        this.creditStatus = creditStatus;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getAccountRib() {
        return accountRib;
    }

    public void setAccountRib(String accountRib) {
        this.accountRib = accountRib;
    }

    @Override
    public String toString() {
        return String.format("Credit ID: %d, Client: %s, Amount: %s, Term: %d months, Status: %s",
                creditId, clientName, loanAmount, loanTermMonths, creditStatus);
    }

    // Simple methods for Manager menu display
    public LocalDate getRequestDate() {
        return startDate; // Use start date as request date for simplicity
    }

    public String getRequestStatus() {
        return creditStatus; // Use credit status as request status
    }
}
