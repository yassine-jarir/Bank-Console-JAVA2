package com.bank.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Credit {
    private String creditId;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private int loanTermMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private String creditStatus;

    // Constructor
    public Credit(String creditId, BigDecimal loanAmount, BigDecimal interestRate, int loanTermMonths, LocalDate startDate, LocalDate endDate, String creditStatus) {
        this.creditId = creditId;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.loanTermMonths = loanTermMonths;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creditStatus = creditStatus;
    }

    // Getters and Setters
    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
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

    public int getLoanTermMonths() {
        return loanTermMonths;
    }

    public void setLoanTermMonths(int loanTermMonths) {
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
}
