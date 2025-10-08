package com.bank.repository.interfaces;

import com.bank.models.CreditPayment;

import java.math.BigDecimal;
import java.util.List;

public interface CreditPaymentRepository {
    List<CreditPayment> getPaymentsByCreditId(Long creditId);
    CreditPayment getNextPendingPayment(Long creditId);
    void createPayment(CreditPayment payment);
    void updatePaymentStatus(Long paymentId, String status);
    void updatePaymentAsPaid(Long paymentId);
    void createMonthlyPayments(Long creditId, int months, java.math.BigDecimal monthlyAmount, java.time.LocalDate startDate , BigDecimal interestRate);
    void updateOverduePayments();
    void addAmountToAccountBalance(Long accountId, java.math.BigDecimal amount);
}
