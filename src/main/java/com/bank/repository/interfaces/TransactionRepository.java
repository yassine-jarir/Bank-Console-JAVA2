package com.bank.repository.interfaces;


import com.bank.models.Transaction;

import java.util.List;

public interface TransactionRepository {
    List<Transaction> getPendingExternalTransfers();
    Transaction getTransactionById(Long transactionId);
    void updateTransactionStatus(Long transactionId, String status);
    void updateAccountBalance(Long accountId, java.math.BigDecimal newBalance);
}
