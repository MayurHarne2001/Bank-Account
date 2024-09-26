package com.example.Bank.Account.Service.Impl;


import com.example.Bank.Account.Entity.BankDetails;
import com.example.Bank.Account.Entity.CustomerDetails;
import com.example.Bank.Account.Entity.DocumentDetails;
import com.example.Bank.Account.Exception.AccountTypeMismatch;
import com.example.Bank.Account.Exception.CustomerNotFoundException;
import com.example.Bank.Account.Exception.DocumentNotFoundException;
import com.example.Bank.Account.Exception.InsufficientFundsException;
import com.example.Bank.Account.Repository.CustomerRepository;
import com.example.Bank.Account.Repository.DocumentRepository;
import com.example.Bank.Account.Service.BankService;

import com.example.Bank.Account.Service.EmailService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.AllArgsConstructor;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;


@Service
@AllArgsConstructor
public class  ServiceImpl implements BankService{
    private CustomerRepository customerRepository;
    private DocumentRepository documentsRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;



    @Override
    public CustomerDetails createCustomerDetails(CustomerDetails customerDetails) {
         validateAccountType(customerDetails);
         customerDetails.setPassword(passwordEncoder.encode(customerDetails.getPassword()));
         CustomerDetails savedDetails= customerRepository.save(customerDetails);

         emailService.sendAccountCreationEmail(savedDetails.getEmail(),savedDetails.getName());
         return savedDetails;
    }

    private void validateAccountType(CustomerDetails customerDetails) {
        String profession = customerDetails.getProfession();
        String accType = customerDetails.getBankDetails().getAccType();

        if ("business".equalsIgnoreCase(profession) && "salary".equalsIgnoreCase(accType)) {
            throw new AccountTypeMismatch("Business professionals cannot open a savings account.");
        }
        if ("student".equalsIgnoreCase(profession) &&
                ("current".equalsIgnoreCase(accType) || "salary".equalsIgnoreCase(accType))) {
            throw new AccountTypeMismatch("Students cannot open a current or salary account.");
        }
        if ("worker".equalsIgnoreCase(profession) &&
                ("current".equalsIgnoreCase(accType) || "salary".equalsIgnoreCase(accType))) {
            throw new AccountTypeMismatch("Workers cannot open a current or salary account.");
        }
    }
    @Override
    public CustomerDetails getCustomerDetails(Long customerId) {
        CustomerDetails customer = customerRepository.findById(customerId)
                .orElseThrow(()->new CustomerNotFoundException("Customer not found"));
        return customer;

    }

    @Override
    public List<CustomerDetails> getAllCustomerDetails() {
          List<CustomerDetails> customer =customerRepository.findAll();
          return customer;

    }


    @Override
    public CustomerDetails updateCustomerDetails(Long customerId, CustomerDetails customerDetails) {
        CustomerDetails existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        validateAccountType(customerDetails);

        existingCustomer.setName(customerDetails.getName());
        existingCustomer.setEmail(customerDetails.getEmail());
        existingCustomer.setPhone(customerDetails.getPhone());
        existingCustomer.setAdharNo(customerDetails.getAdharNo());
        existingCustomer.setPanNo(customerDetails.getPanNo());
        existingCustomer.setProfession(customerDetails.getProfession());

        if (customerDetails.getBankDetails() != null) {
            BankDetails existingBankDetails = existingCustomer.getBankDetails();
            BankDetails newBankDetails = customerDetails.getBankDetails();


            existingBankDetails.setBranchName(newBankDetails.getBranchName());
            existingBankDetails.setAccType(newBankDetails.getAccType());
            existingBankDetails.setBalance(newBankDetails.getBalance());


            existingCustomer.setBankDetails(existingBankDetails);
        }

        if (customerDetails.getDocumentDetails() != null) {
            existingCustomer.setDocumentDetails(customerDetails.getDocumentDetails());

        }

        return customerRepository.save(existingCustomer);
    }

    @Override
    public void deleteCustomerDetails(Long customerId) {
        CustomerDetails customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
       emailService.sendAccountDeletedEmail(customer.getEmail(),customer.getName());
        customerRepository.deleteById(customerId);
    }

    @Override
    public DocumentDetails uploadDocument(Long customerId, String documentType, MultipartFile file) {
        CustomerDetails customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        DocumentDetails document = new DocumentDetails();
        document.setCustomer(customer);
        document.setDocumentType(documentType);
        document.setFileName(file.getOriginalFilename());

        try {
            String base64Data = Base64.getEncoder().encodeToString(file.getBytes());
            document.setData(base64Data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }

        return documentsRepository.save(document);
    }

    @Override
    public Double getAccountBalance(Long customerId) {
        CustomerDetails customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        return customer.getBankDetails().getBalance();

    }

    @Override
    public CustomerDetails addBalance(Long customerId, Double amount) {
        CustomerDetails customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        Double currentBalance = customer.getBankDetails().getBalance();
        customer.getBankDetails().setBalance(currentBalance + amount);
        emailService.sendAddBalanceEmail(customer.getEmail(),customer.getName(),amount);
        return customerRepository.save(customer);
    }


    @Override
    public Double withdrowBalance(Long customerId,Double amount) {
        CustomerDetails customer = customerRepository.findById(customerId)
                .orElseThrow(()->new CustomerNotFoundException("CustomerNotFound"));
        Double currentBalance =customer.getBankDetails().getBalance();

        if (currentBalance < amount) {
            throw new InsufficientFundsException("Insufficient funds for this transaction. Available balance: " + currentBalance);
        }
        customer.getBankDetails().setBalance(currentBalance - amount);
        customerRepository.save(customer);
        return  amount;
    }


    @Override
    public DocumentDetails getDocumentById(Long customerId, Long documentId) {
        CustomerDetails customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        return customer.getDocumentDetails().stream()
                .filter(document -> document.getId().equals(documentId))
                .findFirst()
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
    }

    @Override
    public byte[] generateCustomerDetailsPdf() {
        List<CustomerDetails> customers = customerRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        for (CustomerDetails customer : customers) {
            document.add(new Paragraph("Customer ID: " + customer.getId()));
            document.add(new Paragraph("Name: " + customer.getName()));
            document.add(new Paragraph("Email: " + customer.getEmail()));
            document.add(new Paragraph("Phone: " + customer.getPhone()));
            document.add(new Paragraph("Adhar No: " + customer.getAdharNo()));
            document.add(new Paragraph("PAN No: " + customer.getPanNo()));
            document.add(new Paragraph("Profession: " + customer.getProfession()));
            document.add(new Paragraph("Account Balance: " + customer.getBankDetails().getBalance()));
            document.add(new Paragraph("----------"));
        }

        document.close();

        return out.toByteArray();
    }

    @Override
    public void transferFund(Long fromCustomerId, Long toCustomerId, Double amount) {

        CustomerDetails fromCustomer=customerRepository.findById(fromCustomerId)
                .orElseThrow(()->new CustomerNotFoundException("Sender Customer not found"));

        CustomerDetails toCustomer =customerRepository.findById(toCustomerId)
                .orElseThrow(()->new CustomerNotFoundException("Reciever Customer not found"));

        Double fromBalance=fromCustomer.getBankDetails().getBalance();
        if(fromBalance<amount){
            throw new InsufficientFundsException("Sender has Insufficient fund for this transaction");
        }

        fromCustomer.getBankDetails().setBalance(fromBalance-amount);
        customerRepository.save(fromCustomer);


        Double toBalance=toCustomer.getBankDetails().getBalance();
        toCustomer.getBankDetails().setBalance(toBalance+amount);
        customerRepository.save(toCustomer);

    }

}




