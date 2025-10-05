package com.bank.repository.interfaces;

import com.bank.models.Credit;
import java.util.List;

public interface CreditRepository {
    List<Credit> getPendingCredits();
    Credit getCreditById(Long creditId);
    void updateCreditStatus(Long creditId, String requestStatus, Long approvedBy);
    Credit createCreditRequest(Credit credit);
}
