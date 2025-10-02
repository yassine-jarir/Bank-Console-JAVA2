package com.bank.service;

import com.bank.models.Transaction;
import com.bank.repository.Impl.TransactionRepositoryImpl;

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

}
