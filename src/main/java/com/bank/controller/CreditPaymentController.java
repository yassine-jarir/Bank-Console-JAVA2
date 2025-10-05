package com.bank.controller;

import com.bank.models.CreditPayment;
import com.bank.service.CreditPaymentService;

import java.util.List;

public class CreditPaymentController {
    CreditPaymentService creditPaymentService;

    public CreditPaymentController(CreditPaymentService creditPaymentService) {
        this.creditPaymentService = creditPaymentService;
    }

    // Simple method to pay next installment
    public void payNextInstallment(Long creditId) {
        creditPaymentService.payNextInstallment(creditId);
    }

    // Simple method to show payment history
    public void showPaymentHistory(Long creditId) {
        creditPaymentService.showPaymentHistory(creditId);
    }

    // Simple method to get all payments for a credit
    public List<CreditPayment> getPaymentsForCredit(Long creditId) {
        return creditPaymentService.getPaymentsForCredit(creditId);
    }

    // Simple method to check overdue payments
    public void checkOverduePayments() {
        creditPaymentService.checkOverduePayments();
    }
}
