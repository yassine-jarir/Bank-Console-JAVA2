package com.bank.controller;

import com.bank.models.CreditPayment;
import com.bank.service.CreditPaymentService;

import java.math.BigDecimal;
import java.util.List;

public class CreditPaymentController {
    CreditPaymentService creditPaymentService;

    public CreditPaymentController(CreditPaymentService creditPaymentService) {
        this.creditPaymentService = creditPaymentService;
    }

    public void payNextInstallment(Long creditId) {
        creditPaymentService.payNextInstallment(creditId);
    }

    public void showPaymentHistory(Long creditId) {
        creditPaymentService.showPaymentHistory(creditId);
    }

    public List<CreditPayment> getPaymentsForCredit(Long creditId) {
        return creditPaymentService.getPaymentsForCredit(creditId);
    }

    public void checkOverduePayments() {
        creditPaymentService.checkOverduePayments();
    }

    public void payInstallmentByAmount(Long clientId, BigDecimal amount) {
        creditPaymentService.payInstallmentByAmount(clientId, amount);
    }

    // NEW METHOD: Get next due payment information for a client
    public CreditPayment getNextDuePayment(Long clientId) {
        return creditPaymentService.getNextDuePayment(clientId);
    }
}
