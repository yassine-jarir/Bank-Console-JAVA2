package com.bank.controller;

import com.bank.models.Transaction;
import com.bank.service.TransactionService;

import java.util.List;

public class TransactionController {
    TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    public List<Transaction> getPendingExternalTransfers() {
           List<Transaction> pendingTransactions = transactionService.getPendingExternalTransfers();
           return pendingTransactions;
    }
}
