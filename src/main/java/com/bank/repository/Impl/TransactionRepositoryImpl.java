package com.bank.repository.Impl;

import com.bank.config.DatabaseConfig;
import com.bank.enums.AccountType;
import com.bank.enums.TransactionStatus;
import com.bank.enums.TransactionType;
import com.bank.models.Account;
import com.bank.models.Transaction;
import com.bank.repository.interfaces.TransactionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepositoryImpl implements TransactionRepository {
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    Connection conn;
    public TransactionRepositoryImpl(){
        this.conn = databaseConfig.getConnection();
    }
    public List<Transaction> getPendingExternalTransfers() {
        String sql = "SELECT t.*, " +
                "sa.account_id as source_account_id, sa.account_type as source_account_type, sa.balance as source_balance, sa.currency as source_currency, sa.status as source_status, sa.account_rib as source_rib, " +
                "ta.account_id as target_account_id, ta.account_type as target_account_type, ta.balance as target_balance, ta.currency as target_currency, ta.status as target_status, ta.account_rib as target_rib, " +
                "sc.email as source_client_email, sc.first_name as source_first_name, sc.last_name as source_last_name, " +
                "tc.email as target_client_email, tc.first_name as target_first_name, tc.last_name as target_last_name " +
                "FROM transactions t " +
                "JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "JOIN accounts ta ON t.target_account_id = ta.account_id " +
                "JOIN clients sc ON sa.client_id = sc.client_id " +
                "JOIN clients tc ON ta.client_id = tc.client_id " +
                "WHERE t.transaction_type = 'TRANSFER_EXTERNAL' AND t.status = 'PENDING'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getLong("transaction_id"));
                transaction.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setStatus(TransactionStatus.valueOf(rs.getString("status")));
                transaction.setSourceAccountId(rs.getString("source_account_id"));
                transaction.setTargetAccountId(rs.getString("target_account_id"));
                transaction.setSourceAccountType(AccountType.valueOf(rs.getString("source_account_type")));
                transaction.setTargetAccountType(AccountType.valueOf(rs.getString("target_account_type")));
                transaction.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                // Set client emails
                transaction.setSourceClientEmail(rs.getString("source_client_email"));
                transaction.setTargetClientEmail(rs.getString("target_client_email"));

                // Fill source account details
                Account sourceAccount = new Account();
                sourceAccount.setId(rs.getLong("source_account_id"));
                sourceAccount.setAccountType(AccountType.valueOf(rs.getString("source_account_type")));
                sourceAccount.setBalance(rs.getBigDecimal("source_balance"));
                sourceAccount.setCurrency(rs.getString("source_currency"));
                sourceAccount.setStatus(rs.getString("source_status"));
                sourceAccount.setAccountRib(rs.getString("source_rib"));
                transaction.setSourceAccount(sourceAccount);

                // Fill target account details
                Account targetAccount = new Account();
                targetAccount.setId(rs.getLong("target_account_id"));
                targetAccount.setAccountType(AccountType.valueOf(rs.getString("target_account_type")));
                targetAccount.setBalance(rs.getBigDecimal("target_balance"));
                targetAccount.setCurrency(rs.getString("target_currency"));
                targetAccount.setStatus(rs.getString("target_status"));
                targetAccount.setAccountRib(rs.getString("target_rib"));
                transaction.setTargetAccount(targetAccount);

                transactions.add(transaction);
            }
            return transactions;
        } catch (Exception e) {
            System.out.println("Error fetching pending external transfers: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public Transaction getTransactionById(Long transactionId) {
        String sql = "SELECT t.*, " +
                "sa.account_id as source_account_id, sa.account_type as source_account_type, sa.balance as source_balance, sa.currency as source_currency, sa.status as source_status, sa.account_rib as source_rib, " +
                "ta.account_id as target_account_id, ta.account_type as target_account_type, ta.balance as target_balance, ta.currency as target_currency, ta.status as target_status, ta.account_rib as target_rib, " +
                "sc.email as source_client_email, sc.first_name as source_first_name, sc.last_name as source_last_name, " +
                "tc.email as target_client_email, tc.first_name as target_first_name, tc.last_name as target_last_name " +
                "FROM transactions t " +
                "JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "JOIN accounts ta ON t.target_account_id = ta.account_id " +
                "JOIN clients sc ON sa.client_id = sc.client_id " +
                "JOIN clients tc ON ta.client_id = tc.client_id " +
                "WHERE t.transaction_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, transactionId);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getLong("transaction_id"));
                transaction.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setStatus(TransactionStatus.valueOf(rs.getString("status")));
                transaction.setSourceAccountId(rs.getString("source_account_id"));
                transaction.setTargetAccountId(rs.getString("target_account_id"));
                transaction.setSourceAccountType(AccountType.valueOf(rs.getString("source_account_type")));
                transaction.setTargetAccountType(AccountType.valueOf(rs.getString("target_account_type")));

                // Set client emails
                transaction.setSourceClientEmail(rs.getString("source_client_email"));
                transaction.setTargetClientEmail(rs.getString("target_client_email"));

                // Fill source account details
                Account sourceAccount = new Account();
                sourceAccount.setId(rs.getLong("source_account_id"));
                sourceAccount.setAccountType(AccountType.valueOf(rs.getString("source_account_type")));
                sourceAccount.setBalance(rs.getBigDecimal("source_balance"));
                sourceAccount.setCurrency(rs.getString("source_currency"));
                sourceAccount.setStatus(rs.getString("source_status"));
                sourceAccount.setAccountRib(rs.getString("source_rib"));
                transaction.setSourceAccount(sourceAccount);

                // Fill target account details
                Account targetAccount = new Account();
                targetAccount.setId(rs.getLong("target_account_id"));
                targetAccount.setAccountType(AccountType.valueOf(rs.getString("target_account_type")));
                targetAccount.setBalance(rs.getBigDecimal("target_balance"));
                targetAccount.setCurrency(rs.getString("target_currency"));
                targetAccount.setStatus(rs.getString("target_status"));
                targetAccount.setAccountRib(rs.getString("target_rib"));
                transaction.setTargetAccount(targetAccount);

                return transaction;
            }
        } catch (Exception e) {
            System.out.println("Error fetching transaction by ID: " + e.getMessage());
        }
        return null;
    }

    public void updateTransactionStatus(Long transactionId, String status) {
        String sql = "UPDATE transactions SET status = ? WHERE transaction_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, transactionId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("No transaction found with ID: " + transactionId);
            }
        } catch (Exception e) {
            System.out.println("Error updating transaction status: " + e.getMessage());
            throw new RuntimeException("Failed to update transaction status: " + e.getMessage());
        }
//        update the balance of both account terget and source



    }

    public void updateAccountBalance(Long accountId, java.math.BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setLong(2, accountId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("No account found with ID: " + accountId);
            }
        } catch (Exception e) {
            System.out.println("Error updating account balance: " + e.getMessage());
            throw new RuntimeException("Failed to update account balance: " + e.getMessage());
        }
    }

    public List<Transaction> getAllTransactions() {
        String sql = "SELECT t.*, " +
                "sa.account_rib as source_rib, ta.account_rib as target_rib, " +
                "sc.email as source_client_email, sc.first_name as source_first_name, sc.last_name as source_last_name, " +
                "tc.email as target_client_email, tc.first_name as target_first_name, tc.last_name as target_last_name " +
                "FROM transactions t " +
                "JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "JOIN accounts ta ON t.target_account_id = ta.account_id " +
                "JOIN clients sc ON sa.client_id = sc.client_id " +
                "JOIN clients tc ON ta.client_id = tc.client_id " +
                "ORDER BY t.transaction_date DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            List<Transaction> transactions = new ArrayList<>();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getLong("transaction_id"));
                transaction.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setStatus(TransactionStatus.valueOf(rs.getString("status")));
                transaction.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());

                // Set client information for display
                transaction.setSourceClientEmail(rs.getString("source_client_email"));
                transaction.setTargetClientEmail(rs.getString("target_client_email"));
                transaction.setSourceAccountRib(rs.getString("source_rib"));
                transaction.setTargetAccountRib(rs.getString("target_rib"));

                transactions.add(transaction);
            }
            return transactions;
        } catch (Exception e) {
            System.out.println("Error fetching all transactions: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<Transaction> getAllTransactionsmis(){
        String sql = "SELECT t.*, " +
                "sa.account_rib as source_rib, ta.account_rib as target_rib, " +
                "sc.email as source_client_email, sc.first_name as source_first_name, sc.last_name as source_last_name, " +
                "tc.email as target_client_email, tc.first_name as target_first_name, tc.last_name as target_last_name " +
                "FROM transactions t " +
                "JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "JOIN accounts ta ON t.target_account_id = ta.account_id " +
                "JOIN clients sc ON sa.client_id = sc.client_id " +
                "JOIN clients tc ON ta.client_id = tc.client_id " +
                "ORDER BY t.transaction_date DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            List<Transaction> transactions = new ArrayList<>();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getLong("transaction_id"));
                transaction.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setStatus(TransactionStatus.valueOf(rs.getString("status")));
                transaction.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());

                // Set client information for display
                transaction.setSourceClientEmail(rs.getString("source_client_email"));
                transaction.setTargetClientEmail(rs.getString("target_client_email"));
                transaction.setSourceAccountRib(rs.getString("source_rib"));
                transaction.setTargetAccountRib(rs.getString("target_rib"));

                transactions.add(transaction);
            }
            return transactions;
        } catch (Exception e) {
            System.out.println("Error fetching all transactions: " + e.getMessage());
        }
        return new ArrayList<>();
    }

}
