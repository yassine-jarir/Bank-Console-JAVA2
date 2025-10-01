package com.bank.repository.interfaces;
import com.bank.enums.Role;
import com.bank.models.Account;
import com.bank.models.Client;
import com.bank.models.User;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRepository {
     Account getAccountByRIB(String RIB );
     Account createAccount(Account account, Client client);
         void updateAccountBalance(String rib, BigDecimal newBalance);
        void insertTransaction(String rib, BigDecimal amount, String type , Long accountId);
        void internalTransfer(Client client, String sourceRib, String destRib, BigDecimal amount);
}
