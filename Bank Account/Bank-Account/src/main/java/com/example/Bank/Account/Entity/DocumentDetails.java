package com.example.Bank.Account.Entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class DocumentDetails {

    @Id
    @GeneratedValue
    private Long id;
    private String documentType;

    private String fileName;

    @Lob
    private  String data;

    @ManyToOne
    @JsonBackReference
    private  CustomerDetails customer;

}