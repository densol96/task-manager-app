package com.accenture.backend.controller.advice;

import com.accenture.backend.model.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorMessage> usernameNotFoundHandler() {
        ErrorMessage errorDetails = new ErrorMessage("Username or email already in use");
        return ResponseEntity.badRequest().body(errorDetails);
    }
}
