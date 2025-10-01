package com.bank.repository.Impl;

import com.bank.config.DatabaseConfig;
import com.bank.enums.AccountType;
import com.bank.models.Account;
import com.bank.models.Client;
import com.bank.repository.interfaces.AccountRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class AccountRepositoryImpl implements AccountRepository {
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    Connection connection;

    public AccountRepositoryImpl() {
        this.connection = databaseConfig.getConnection();
    }

//
    public Account getAccountByRIB(String rib) {
        String sql = "SELECT * FROM  clients c JOIN accounts a ON c.client_id = a.client_id WHERE a.account_rib = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, rib);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                Account account = new Account();
                account.setAccountRib(rs.getString("account_rib"));
                account.setAccountType(AccountType.valueOf(rs.getString("account_type")));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setCurrency(rs.getString("currency"));
                account.setStatus(rs.getString("status"));
                account.setCustomerId(rs.getLong("client_id"));
                account.setId(rs.getLong("account_id"));
                return account;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Account createAccount(Account account , Client client) {
        String sql = "INSERT INTO accounts(account_type, balance, currency, status, client_id, account_rib) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, account.getAccountType().toString());
            stmt.setBigDecimal(2, account.getBalance());
            stmt.setString(3, account.getCurrency());
            stmt.setString(4, account.getStatus());
            stmt.setLong(5, client.getId());

            stmt.setString(6, account.getAccountRib());
            stmt.executeUpdate();
            return account;
        } catch (Exception e) {
         System.out.println(e.getMessage());
            return null;
        }
    }


    public void updateAccountBalance(String rib, BigDecimal newBalance ) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_rib = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setString(2, rib);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertTransaction(String rib, BigDecimal amount, String type , Long accountId) {
        String sql = "INSERT INTO transactions(source_account_id , target_account_id , transaction_type , status  , amount, transaction_date) VALUES (?, ?,?, ?, ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            stmt.setLong(2, accountId);
            stmt.setString(3, type);
            stmt.setString(4, "COMPLETED");
            stmt.setBigDecimal(5, amount);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

public void internalTransfer(Client client, String sourceRib, String destRib, BigDecimal amount){
    Account sourceAccount = getAccountByRIB(sourceRib);
    Account destAccount = getAccountByRIB(destRib);
    if (sourceAccount == null) {
        throw new RuntimeException("Source account not found for RIB: " + sourceRib);
    }
    if (destAccount == null) {
        throw new RuntimeException("Destination account not found for RIB: " + destRib);
    }
    if (sourceAccount.getBalance().compareTo(amount) < 0) {
        throw new RuntimeException("Insufficient funds in source account. Current balance: " + sourceAccount.getBalance());
    }
    sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
    destAccount.setBalance(destAccount.getBalance().add(amount));
    try {
        updateAccountBalance(sourceAccount.getAccountRib(), sourceAccount.getBalance());
        updateAccountBalance(destAccount.getAccountRib(), destAccount.getBalance());
        insertTransaction(sourceAccount.getAccountRib(), amount, "TRANSFER internal" , sourceAccount.getId());
        insertTransaction(destAccount.getAccountRib(), amount, "TRANSFER internal" , destAccount.getId());
        System.out.println("Transferred " + amount + " from Account : " + sourceAccount.getAccountType() + " to Account: " + destAccount.getAccountType() + " successfully.");
    } catch (Exception e) {
        throw new RuntimeException("Failed to transfer amount: " + e.getMessage());
    }
}
}
