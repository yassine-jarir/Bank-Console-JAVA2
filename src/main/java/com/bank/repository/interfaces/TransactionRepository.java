package com.bank.repository.interfaces;


import com.bank.models.Transaction;

import java.util.List;

public interface TransactionRepository {
    List<Transaction> getPendingExternalTransfers();
}
