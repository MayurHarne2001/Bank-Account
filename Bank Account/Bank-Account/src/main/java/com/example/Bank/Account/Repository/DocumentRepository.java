package com.example.Bank.Account.Repository;


import com.example.Bank.Account.Entity.DocumentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentDetails,Long> {
}
