package com.bank.controller;

import com.bank.models.Client;
import com.bank.models.Transaction;
import com.bank.repository.Impl.TransactionRepositoryImpl;
import com.bank.service.TransactionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionController {
    TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public List<Transaction> getPendingExternalTransfers() {
           List<Transaction> pendingTransactions = transactionService.getPendingExternalTransfers();
           return pendingTransactions;
    }

    public void approveTransaction(Long transactionId) {
        transactionService.approveTransaction(transactionId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

}
