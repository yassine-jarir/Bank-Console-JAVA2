package com.bank.repository.interfaces;
import com.bank.models.Account;
import java.util.List;

public interface AccountRepository {
    Account createAccount(Account account);
    Account getAccountById(String accountId);
    List<Account> getAllAccounts();
    void updateAccount(Account account);
    void deleteAccount(String accountId);
}
