package com.example.Bank.Account.Repository;

import com.example.Bank.Account.Entity.CustomerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerDetails, Long> {
    Optional<CustomerDetails> findByName(String name);
}
