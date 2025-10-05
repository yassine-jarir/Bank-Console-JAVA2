package com.bank.service;

import com.bank.models.Transaction;
import com.bank.repository.Impl.TransactionRepositoryImpl;

import java.math.BigDecimal;
import java.util.List;

public class TransactionService {
    TransactionRepositoryImpl transactionRepository;
    public TransactionService(TransactionRepositoryImpl transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getPendingExternalTransfers() {
           List<Transaction> pendingTransactions = transactionRepository.getPendingExternalTransfers();
           return pendingTransactions;
    }

    public void approveTransaction(Long transactionId) {
        try {
            // Get the transaction details
            Transaction transaction = transactionRepository.getTransactionById(transactionId);
            if (transaction == null) {
                throw new RuntimeException("Transaction not found with ID: " + transactionId);
            }

            // Verify transaction is still pending
            if (!"PENDING".equals(transaction.getStatus().name())) {
                throw new RuntimeException("Transaction is not in PENDING status. Current status: " + transaction.getStatus());
            }

            // Get source and target account balances
            BigDecimal sourceBalance = transaction.getSourceAccount().getBalance();
            BigDecimal targetBalance = transaction.getTargetAccount().getBalance();
            BigDecimal transferAmount = transaction.getAmount();

            // Verify sufficient funds (this should have been checked before, but double-check)
            if (sourceBalance.compareTo(transferAmount) < 0) {
                throw new RuntimeException("Insufficient funds in source account. Available: " + sourceBalance + ", Required: " + transferAmount);
            }

            // Calculate new balances
            BigDecimal newSourceBalance = sourceBalance.subtract(transferAmount);
            BigDecimal newTargetBalance = targetBalance.add(transferAmount);

            // Update account balances
            transactionRepository.updateAccountBalance(transaction.getSourceAccount().getId(), newSourceBalance);
            transactionRepository.updateAccountBalance(transaction.getTargetAccount().getId(), newTargetBalance);

            // Update transaction status to SETTLED
            transactionRepository.updateTransactionStatus(transactionId, "COMPLETED");

            System.out.println("Transaction approved successfully!");
            System.out.printf("Transferred %s from %s (new balance: %s) to %s (new balance: %s)%n",
                    transferAmount,
                    transaction.getSourceAccount().getAccountRib(),
                    newSourceBalance,
                    transaction.getTargetAccount().getAccountRib(),
                    newTargetBalance);

        } catch (Exception e) {
            System.out.println("Failed to approve transaction: " + e.getMessage());
            throw new RuntimeException("Transaction approval failed: " + e.getMessage());
        }
    }

    // NEW METHOD: Get all transactions for admin view
    public List<Transaction> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }
}
