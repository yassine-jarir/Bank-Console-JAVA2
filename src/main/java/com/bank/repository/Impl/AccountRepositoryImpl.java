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

    public Account createAccount(Account account, Client client) {
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

    public void updateAccountBalance(String rib, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_rib = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setString(2, rib);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertTransaction(String rib, BigDecimal amount, String type, Long accountId) {
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

    public void internalTransfer(Client client, String sourceRib, String destRib, BigDecimal amount) {
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
            insertTransaction(sourceAccount.getAccountRib(), amount, "TRANSFER internal", sourceAccount.getId());
            insertTransaction(destAccount.getAccountRib(), amount, "TRANSFER internal", destAccount.getId());
            System.out.println("Transferred " + amount + " from Account : " + sourceAccount.getAccountType() + " to Account: " + destAccount.getAccountType() + " successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to transfer amount: " + e.getMessage());
        }
    }

    public void insertExternalTransaction(Account sourceAccount, Account destAccount, BigDecimal amount , int fee_id) {
        String sql = "INSERT INTO transactions(source_account_id , target_account_id , transaction_type , status  , amount, transaction_date , fee_id) VALUES (?, ?,?, ?, ?, NOW(), ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, sourceAccount.getId());
            stmt.setLong(2, destAccount.getId());
            stmt.setString(3,  "TRANSFER EXTERNAL");
            stmt.setString(4, "PENDING");
            stmt.setBigDecimal(5, amount);
            stmt.setInt(6, fee_id);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void externalTransfer(Client client, String sourceRib, String destRib, BigDecimal amount) {
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
        String sql = "SELECT fee_id , value FROM fee_rules WHERE operation_type = 'EXTERNAL_TRANSFER'";
        BigDecimal feeValue = null;
        int fee_id = 0;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            if (rs.next()) {
                feeValue = rs.getBigDecimal("value");
                 fee_id = rs.getInt("fees_id");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
//        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount.add(feeValue)));
//        destAccount.setBalance(sourceAccount.getBalance().add(amount));
        try {
//            updateAccountBalance(sourceAccount.getAccountRib(), sourceAccount.getBalance());
//            updateAccountBalance(destAccount.getAccountRib(), destAccount.getBalance());
            insertExternalTransaction(sourceAccount, destAccount, amount, fee_id);
            System.out.println("Your request for transferring " + amount + " from Account : " + sourceAccount.getAccountRib() + " to Account: " + destAccount.getAccountRib() + " is being processed.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to transfer amount: " + e.getMessage());
        }
    }

    public void createAccountForClient(Client client , String RIB) {
        String sql = "SELECT * FROM accounts WHERE client_id = ? AND account_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, client.getId());
            stmt.setString(2, AccountType.SAVINGS.toString());
            var rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Client already has a savings account. Cannot create another one.");
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Account account = new Account();
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(new BigDecimal("0.00"));
        account.setCurrency("MAD");
        account.setStatus("ACTIVE");
        account.setCustomerId(client.getId());
        account.setAccountRib(RIB);
        createAccount(account, client);

    }
}
