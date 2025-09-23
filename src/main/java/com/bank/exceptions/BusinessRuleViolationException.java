package com.bank.exceptions;

public class BusinessRuleViolationException extends Exception {
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
