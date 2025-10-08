package com.bank.service;

import com.bank.models.CreditPayment;
import com.bank.repository.Impl.CreditPaymentRepositoryImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class CreditPaymentService {
    CreditPaymentRepositoryImpl creditPaymentRepository;

    public CreditPaymentService(CreditPaymentRepositoryImpl creditPaymentRepository) {
        this.creditPaymentRepository = creditPaymentRepository;
    }

    public void createMonthlyPayments(Long creditId, BigDecimal loanAmount, int termMonths, LocalDate startDate) {
        BigDecimal monthlyAmount = loanAmount.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);

        creditPaymentRepository.createMonthlyPayments(creditId, termMonths, monthlyAmount, startDate , BigDecimal.ZERO);

        System.out.println("Created " + termMonths + " monthly payments of " + monthlyAmount + " each");
    }

    public List<CreditPayment> getPaymentsForCredit(Long creditId) {
        return creditPaymentRepository.getPaymentsByCreditId(creditId);
    }

    public void payNextInstallment(Long creditId) {
        creditPaymentRepository.updateOverduePayments();

        CreditPayment nextPayment = creditPaymentRepository.getNextPendingPayment(creditId);

        if (nextPayment == null) {
            System.out.println("No pending payments found for this credit!");
            return;
        }
        creditPaymentRepository.updatePaymentAsPaid(nextPayment.getPaymentId());
        System.out.println("--- Payment successful!");
        System.out.println("Amount paid: " + nextPayment.getAmount());
        System.out.println("Due date was: " + nextPayment.getDueDate());
        System.out.println("Payment date: " + LocalDate.now());
    }

    public void payInstallmentByAmount(Long clientId, BigDecimal paymentAmount) {
        creditPaymentRepository.updateOverduePayments();

        CreditPayment matchingPayment = creditPaymentRepository.getPendingPaymentByClientAndAmount(clientId, paymentAmount);

        if (matchingPayment == null) {
            System.out.println("! No pending payment found for amount: " + paymentAmount + " MAD");
            System.out.println("Please check your payment amount or contact support.");
            return;
        }
        creditPaymentRepository.updatePaymentAsPaid(matchingPayment.getPaymentId());

        System.out.println("--- Payment successful!");
    }

    public void showPaymentHistory(Long creditId) {
        List<CreditPayment> payments = getPaymentsForCredit(creditId);

        if (payments.isEmpty()) {
            System.out.println("No payments found for this credit.");
            return;
        }

        System.out.println("\n=== Payment History ===");
        System.out.println("Credit ID: " + creditId);
        System.out.println("=" .repeat(60));

        for (int i = 0; i < payments.size(); i++) {
            CreditPayment payment = payments.get(i);
            System.out.printf("%d. Due: %s | Amount: %s | Status: %s",
                    (i + 1), payment.getDueDate(), payment.getAmount(), payment.getStatus());

            if (payment.getPaymentDate() != null) {
                System.out.printf(" | Paid: %s", payment.getPaymentDate());
            }
            System.out.println();
        }

        long paidCount = payments.stream().mapToLong(p -> "PAID".equals(p.getStatus()) ? 1 : 0).sum();
        long pendingCount = payments.stream().mapToLong(p -> "PENDING".equals(p.getStatus()) ? 1 : 0).sum();
        long lateCount = payments.stream().mapToLong(p -> "LATE".equals(p.getStatus()) ? 1 : 0).sum();

        System.out.println("-" .repeat(60));
        System.out.println("Summary: Paid=" + paidCount + " | Pending=" + pendingCount + " | Late=" + lateCount);
    }

    public void checkOverduePayments() {
        creditPaymentRepository.checkOverduePayments();
    }

    public CreditPayment getNextDuePayment(Long clientId) {
        return creditPaymentRepository.getNextPendingPaymentByClient(clientId);
    }
}
