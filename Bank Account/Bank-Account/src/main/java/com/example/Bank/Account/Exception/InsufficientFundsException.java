package com.example.Bank.Account.Exception;

public class InsufficientFundsException extends RuntimeException{
       public InsufficientFundsException(String message){
           super(message);
       }
}
