package com.bank.repository.Impl;

import com.bank.models.Transaction;
import com.bank.repository.interfaces.TransactionRepository;

import java.util.List;

public class TransactionRepositoryImpl implements TransactionRepository {

    public List<Transaction> getPendingExternalTransfers() {
        String sql = "SELECT * FROM transactions WHERE type = 'EXTERNAL_' AND status = 'pending'";
        return null;
    }
}
