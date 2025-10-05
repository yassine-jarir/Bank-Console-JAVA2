package com.bank.controller;

import com.bank.models.Credit;
import com.bank.service.CreditService;

import java.util.List;

public class CreditController {
    CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    public List<Credit> getPendingCredits() {
        return creditService.getPendingCredits();
    }

    public void approveCredit(Long creditId, Long managerId) {
        creditService.approveCredit(creditId, managerId);
    }

    public void denyCredit(Long creditId, Long managerId) {
        creditService.denyCredit(creditId, managerId);
    }

    public Credit createCreditRequest(Credit credit) {
        return creditService.createCreditRequest(credit);
    }
}
