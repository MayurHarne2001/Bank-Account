package com.example.Bank.Account.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class BankDetails {

    @Id
    @GeneratedValue
    private Long accNo;

    private String branchName;
    private String accType;
    private Double balance;

}
