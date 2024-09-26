package com.example.Bank.Account.controller;



import com.example.Bank.Account.Entity.CustomerDetails;
import com.example.Bank.Account.Entity.DocumentDetails;
import com.example.Bank.Account.Response.CommonResponse;
import com.example.Bank.Account.Response.ResponseUtil;
import com.example.Bank.Account.Service.BankService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.Base64;
import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/api/details")
public class BankController {
    private BankService bankService;

    // api for account creation
    @PostMapping("/customerDetails")
    public ResponseEntity<CommonResponse<CustomerDetails>> createCustomerDetails(@RequestBody CustomerDetails customerDetails) {
            CustomerDetails savedDetails = bankService.createCustomerDetails(customerDetails);
             return ResponseUtil.buildResponse(HttpStatus.CREATED, "Customer created successfully", savedDetails);

    }
    // for getting perticular customer details
    @GetMapping("/customerDetails/{customerId}")
    public ResponseEntity<CommonResponse<CustomerDetails>> getCustomerDetails(@PathVariable Long customerId){
          CustomerDetails savedDetails=bankService.getCustomerDetails(customerId);
          return ResponseUtil.buildResponse(HttpStatus.OK,"Customer details retrieved successfully", savedDetails);
    }

    //for getting all customer details
    @GetMapping("/customerDetails")
    public ResponseEntity<CommonResponse<List<CustomerDetails>>> getAllCustomerDetails(){
                  List<CustomerDetails> customer =bankService.getAllCustomerDetails();
                  return ResponseUtil.buildResponse(HttpStatus.OK,"Customer details retrived successfully",customer);
    }
    // for updating customer details
    @PutMapping("/customerDetails/{customerId}")
    public ResponseEntity<CommonResponse<CustomerDetails>> updateCustomerDetails(@PathVariable Long customerId, @RequestBody CustomerDetails customerDetails) {
        CustomerDetails updatedCustomer = bankService.updateCustomerDetails(customerId, customerDetails);
        return ResponseUtil.buildResponse(HttpStatus.OK,"Customer details updated successfully",updatedCustomer);
    }

    // for deleting account
    @DeleteMapping("/delete/customerDetails/{customerId}")
    public ResponseEntity<String> deleteCustomerDetails(@PathVariable Long customerId){
                   bankService.deleteCustomerDetails(customerId);
                   return ResponseEntity.ok("Account Deleted Successfully");
    }
    // for uploading document
    @PostMapping("/uploadDocument/{customerId}")
    public ResponseEntity<String> uploadDocument(@PathVariable Long customerId,
                                                    @RequestParam String documentType,
                                                    @RequestParam MultipartFile file) {
        DocumentDetails document = bankService.uploadDocument(customerId, documentType, file);
        return new ResponseEntity<>("Document uploaded successfully", HttpStatus.OK);
    }
     // for fetching bank balance
    @GetMapping("/balance/{customerId}")
    ResponseEntity<CommonResponse<Double>> getAccountBalance(@PathVariable Long customerId){
        Double balance = bankService.getAccountBalance(customerId);
        return ResponseUtil.buildResponse(HttpStatus.OK,"Balance fetched successfully",balance);
    }
    //api for adding balance
    @PostMapping("/addBalance/{customerId}")
    ResponseEntity<String> addBalance(@PathVariable Long customerId,
                                               @RequestParam Double amount){

         CustomerDetails update =bankService.addBalance(customerId,amount);
         return new ResponseEntity<>("Amount Deposited Succcessfully",HttpStatus.OK);

    }
    // for withdrowing amount
    @GetMapping("/withdraw/{customerId}")
    ResponseEntity<CommonResponse<Double>> withdrawBalance(@PathVariable Long customerId ,
                                           @RequestParam Double amount){
        Double Balance =bankService.withdrowBalance(customerId,amount);
        return ResponseUtil.buildResponse(HttpStatus.OK,"Amount withdrowed successfully",Balance);
    }

    //api fro transfering fund
    @PostMapping("/transferFund")
    ResponseEntity<String> transferFund(@RequestParam Long fromCustomerId,
                                        @RequestParam Long toCustomerId,
                                        @RequestParam Double amount){
        bankService.transferFund(fromCustomerId,toCustomerId,amount);
        return new ResponseEntity<>("Fund transfered Successfully",HttpStatus.OK);
    }

    // for downloading of files which are uploaded i.e adhar or pan card
    @GetMapping("/downloadDocument/{customerId}/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long customerId, @PathVariable Long documentId) {
        DocumentDetails document = bankService.getDocumentById(customerId, documentId);


        byte[] data = Base64.getDecoder().decode(document.getData());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", document.getFileName());

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }


    //FOR getting customer data in pdf format
    @GetMapping("/downloadCustomerDetailsPdf")
    public ResponseEntity<byte[]> downloadCustomerDetailsPdf() {
        byte[] pdfData = bankService.generateCustomerDetailsPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "customer-details.pdf");

        return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
    }


}
