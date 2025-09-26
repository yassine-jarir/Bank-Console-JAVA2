package com.bank.repository.interfaces;
import com.bank.enums.Role;
import com.bank.models.Account;
import com.bank.models.User;

import java.util.List;

public interface AccountRepository {
     Account getAccountByRIB(String RIB , String password);
}
