package com.accenture.backend.controller.advice;

import com.accenture.backend.dto.response.BasicErrorDto;
import com.accenture.backend.exception.EmailAlreadyInUseException;
import com.accenture.backend.util.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorMessage> usernameNotFoundHandler() {
        ErrorMessage errorDetails = new ErrorMessage("There is no user with such email");
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorMessage> EmailAlreadyInUseHandler() {
        ErrorMessage errorDetails = new ErrorMessage("Email already in use");
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessage> BadCredentialsHandler() {
        ErrorMessage errorDetails = new ErrorMessage("You password is incorrect");
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BasicErrorDto> handleUnexpectedException(Exception e) {
        System.out.println("UNEXPECTED EXCEPTION: " + e.getClass().getSimpleName() + " --- " + e.getMessage());
        return new ResponseEntity<>(new BasicErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
