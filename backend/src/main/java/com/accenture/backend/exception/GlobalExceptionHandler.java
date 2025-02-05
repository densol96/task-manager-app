package com.accenture.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.accenture.backend.dto.response.BasicErrorDto;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BasicErrorDto> handleUnexpectedException(Exception e) {
        System.out.println("UNEXPECTED EXCEPTION: " + e.getClass().getSimpleName() + " --- " + e.getMessage());
        return new ResponseEntity<>(new BasicErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
