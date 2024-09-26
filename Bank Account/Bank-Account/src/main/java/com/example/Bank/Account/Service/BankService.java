package com.example.Bank.Account.Service;


import com.example.Bank.Account.Entity.CustomerDetails;
import com.example.Bank.Account.Entity.DocumentDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;


public interface BankService {

     CustomerDetails createCustomerDetails(CustomerDetails customerDetails);
     CustomerDetails getCustomerDetails(Long customerId);
     List<CustomerDetails> getAllCustomerDetails();
     CustomerDetails updateCustomerDetails(Long customerId, CustomerDetails customerDetails);
     void deleteCustomerDetails(Long customerId);
     DocumentDetails uploadDocument(Long customerId, String documentType, MultipartFile file);
     Double getAccountBalance(Long customerId);
     CustomerDetails addBalance(Long customerId,Double amount);
     Double withdrowBalance(Long customerId,Double amount);
     DocumentDetails getDocumentById(Long customerId, Long documentId);
     public byte[] generateCustomerDetailsPdf();
     public void transferFund(Long fromCustomerId,Long toCustomerId,Double amount);

}
