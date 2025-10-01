package com.bank.service;

import com.bank.enums.AccountType;
import com.bank.models.Account;
import com.bank.models.Client;
import com.bank.repository.Impl.AccountRepositoryImpl;
import com.bank.repository.interfaces.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public class AccountService {
    // Account service methods
    static AccountRepositoryImpl accountRepositoryImpl;

    public AccountService(AccountRepositoryImpl accountRepositoryImpl){
        this.accountRepositoryImpl = accountRepositoryImpl;

    }

    public class RibGenerator {

        private static final Random random = new Random();

        public static String createRib() {
                String countryCode = "MA";
            int checkDigits = 10 + random.nextInt(89);
            String bankCode = "3000";
            String branchCode = "4000";

            StringBuilder accountNumber = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                accountNumber.append(random.nextInt(10));
            }

            String rib = String.format("%s%02d %s %s %s",
                    countryCode, checkDigits, bankCode, branchCode, accountNumber.toString());

            return rib;
        }
    }

    public static Account createAccountForClient(Client client , boolean savingAccount){
        Account account = new Account();
        account.setAccountType(AccountType.CURRENT);
        account.setBalance(new BigDecimal("0.00"));
        account.setCurrency("MAD");
        account.setStatus("ACTIVE");
        account.setCustomerId(client.getId());
        account.setAccountRib(RibGenerator.createRib());

        accountRepositoryImpl.createAccount(account , client);
        if (savingAccount){
            Account savingAcc = new Account();
            savingAcc.setAccountType(AccountType.SAVINGS);
            savingAcc.setBalance(new BigDecimal("0.00"));
            savingAcc.setCurrency("MAD");
            savingAcc.setStatus("ACTIVE");
            savingAcc.setCustomerId(client.getId());
            savingAcc.setAccountRib(RibGenerator.createRib());
            accountRepositoryImpl.createAccount(savingAcc , client);
        }
       return account;
    }

    public void deposit(String rib, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        Account account = accountRepositoryImpl.getAccountByRIB(rib);
        if (account == null) {
            throw new RuntimeException("Account not found for RIB: " + rib);
        }
        account.setBalance(account.getBalance().add(amount));
        try {
            accountRepositoryImpl.updateAccountBalance(account.getAccountRib(), account.getBalance() );
            accountRepositoryImpl.insertTransaction(account.getAccountRib(), amount, "DEPOSIT" , account.getId());
            System.out.println("Deposited " + amount + " to account RIB: " + rib + "successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to deposit amount: " + e.getMessage());
        }

    }
    public   void  withdraw(String rib, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive.");
        }
        Account account = accountRepositoryImpl.getAccountByRIB(rib);
        if (account == null) {
            throw new RuntimeException("Account not found for RIB: " + rib);
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds. Current balance: " + account.getBalance());
        }
        account.setBalance(account.getBalance().subtract(amount));
        try {
            accountRepositoryImpl.updateAccountBalance(account.getAccountRib(), account.getBalance());
            accountRepositoryImpl.insertTransaction(account.getAccountRib(), amount, "WITHDRAW", account.getId());
            System.out.println("Withdrew " + amount + " from account RIB: " + rib + " successfully. New balance: " + account.getBalance());
        } catch (Exception e) {
            throw new RuntimeException("Failed to withdraw amount: " + e.getMessage());
        }
    }
}
