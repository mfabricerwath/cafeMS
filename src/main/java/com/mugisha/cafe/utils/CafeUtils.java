package com.mugisha.cafe.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

public class CafeUtils {

    private CafeUtils() {
    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<>(responseMessage, httpStatus);
        
    }

}