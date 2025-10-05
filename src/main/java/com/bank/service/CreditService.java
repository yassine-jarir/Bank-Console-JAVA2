package com.bank.service;

import com.bank.models.Credit;
import com.bank.repository.Impl.CreditRepositoryImpl;
import com.bank.repository.Impl.CreditPaymentRepositoryImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CreditService {
    CreditRepositoryImpl creditRepository;
    CreditPaymentRepositoryImpl creditPaymentRepository;

    public CreditService(CreditRepositoryImpl creditRepository) {
        this.creditRepository = creditRepository;
        this.creditPaymentRepository = new CreditPaymentRepositoryImpl(); // Simple initialization
    }

    public List<Credit> getPendingCredits() {
        return creditRepository.getPendingCredits();
    }

    public void approveCredit(Long creditId, Long managerId) {
        try {
            // Get the credit details
            Credit credit = creditRepository.getCreditById(creditId);
            if (credit == null) {
                throw new RuntimeException("Credit not found with ID: " + creditId);
            }

            // Verify credit is still pending
            if (!"PENDING".equals(credit.getCreditStatus())) {
                throw new RuntimeException("Credit is not in PENDING status. Current status: " + credit.getCreditStatus());
            }

            // Update credit status to ACTIVE (approved)
            creditRepository.updateCreditStatus(creditId, "ACTIVE", managerId);

            // NEW: Create monthly payment schedule when credit is approved
            BigDecimal monthlyAmount = credit.getLoanAmount().divide(
                BigDecimal.valueOf(credit.getLoanTermMonths()), 2, RoundingMode.HALF_UP);

            creditPaymentRepository.createMonthlyPayments(
                creditId,
                credit.getLoanTermMonths(),
                monthlyAmount,
                credit.getStartDate()
            );

            System.out.println("Credit approved successfully!");
            System.out.printf("Credit ID: %d for client %s has been APPROVED%n",
                    creditId, credit.getClientName());
            System.out.printf("Loan Amount: %s, Term: %d months%n",
                    credit.getLoanAmount(), credit.getLoanTermMonths());
            System.out.printf("Monthly Payment: %s (created %d payment schedule)%n",
                    monthlyAmount, credit.getLoanTermMonths());

        } catch (Exception e) {
            System.out.println("Failed to approve credit: " + e.getMessage());
            throw new RuntimeException("Credit approval failed: " + e.getMessage());
        }
    }

    public void denyCredit(Long creditId, Long managerId) {
        try {
            // Get the credit details
            Credit credit = creditRepository.getCreditById(creditId);
            if (credit == null) {
                throw new RuntimeException("Credit not found with ID: " + creditId);
            }

            // Verify credit is still pending
            if (!"PENDING".equals(credit.getCreditStatus())) {
                throw new RuntimeException("Credit is not in PENDING status. Current status: " + credit.getCreditStatus());
            }

            // Update credit status to REJECTED (denied)
            creditRepository.updateCreditStatus(creditId, "REJECTED", managerId);

            System.out.println("Credit denied successfully!");
            System.out.printf("Credit ID: %d for client %s has been REJECTED%n",
                    creditId, credit.getClientName());

        } catch (Exception e) {
            System.out.println("Failed to deny credit: " + e.getMessage());
            throw new RuntimeException("Credit denial failed: " + e.getMessage());
        }
    }

    public Credit createCreditRequest(Credit credit) {
        return creditRepository.createCreditRequest(credit);
    }
}
