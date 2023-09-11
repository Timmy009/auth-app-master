package com.example.ecommerce.exception;


import com.example.ecommerce.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception exception){
        ApiResponse<?> response = new ApiResponse<>(exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.badRequest().body(response);
    }


}
