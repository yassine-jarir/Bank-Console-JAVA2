package com.bank.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreditPayment {
    private Long paymentId;
    private Long creditId;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String status; // PENDING, PAID, LATE, MISSED

    // Simple constructor
    public CreditPayment() {}

    // Constructor for creating payments
    public CreditPayment(Long creditId, LocalDate dueDate, BigDecimal amount) {
        this.creditId = creditId;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = "PENDING";
    }

    // Simple getters and setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getCreditId() {
        return creditId;
    }

    public void setCreditId(Long creditId) {
        this.creditId = creditId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Payment: Due %s, Amount: %s, Status: %s",
                dueDate, amount, status);
    }
}
