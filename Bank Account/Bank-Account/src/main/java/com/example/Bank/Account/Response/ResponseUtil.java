package com.example.Bank.Account.Response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static <T> ResponseEntity<CommonResponse<T>> buildResponse(HttpStatus status, String message, T data) {
        CommonResponse<T> response = new CommonResponse<>(status.value(), message, data);
        return new ResponseEntity<>(response, status);
    }
}