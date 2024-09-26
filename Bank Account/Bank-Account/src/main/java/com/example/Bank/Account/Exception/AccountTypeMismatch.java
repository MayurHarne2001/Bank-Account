package com.example.Bank.Account.Exception;

    public class AccountTypeMismatch extends RuntimeException {
        public AccountTypeMismatch(String message) {
            super(message);
        }
    }
