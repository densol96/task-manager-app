package com.accenture.backend.controller.advice;

import com.accenture.backend.dto.response.BasicErrorDto;
import com.accenture.backend.dto.response.ValidationErrorResponseDto;
import com.accenture.backend.exception.EmailAlreadyInUseException;
import com.accenture.backend.exception.ResponseStatusExceptionIfProjectMember;
import com.accenture.backend.exception.ResponseStatusExceptionIfProjectMemberManagerOrOwner;
import com.accenture.backend.util.ErrorMessage;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorMessage> usernameNotFoundHandler() {
        ErrorMessage errorDetails = new ErrorMessage("There is no user with such email");
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> fieldsAndMessages = new LinkedHashMap<>();
        bindingResult.getFieldErrors().stream()
                .forEach(fieldError -> fieldsAndMessages.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return new ResponseEntity<>(new ValidationErrorResponseDto(fieldsAndMessages),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
    public ResponseEntity<BasicErrorDto> handleHttpMessageNotReadableException(Exception e) {
        String message = e.getMessage().contains("request body is missing") ? "Required request body is missing."
                : "Invalid input format.";
        return new ResponseEntity<>(new BasicErrorDto(message), HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(ResponseStatusExceptionIfProjectMember.class)
    public ResponseEntity<ErrorMessage> responseStatusExceptionHandlerIfProjectMember() {
        ErrorMessage errorDetails = new ErrorMessage("User is not a member of this project");
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResponseStatusExceptionIfProjectMemberManagerOrOwner.class)
    public ResponseEntity<ErrorMessage> responseStatusExceptionHandlerProjectMemberRolePermissions() {
        ErrorMessage errorDetails = new ErrorMessage("Permission denied. Only OWNER or MANAGER of the Project can do this operation.");
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorMessage> responseStatusException(Exception e) {
        ErrorMessage errorDetails = new ErrorMessage(e.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /*@ExceptionHandler(Exception.class)
    public ResponseEntity<BasicErrorDto> handleUnexpectedException(Exception e) {
        System.out.println("UNEXPECTED EXCEPTION: " + e.getClass().getSimpleName() + " --- " + e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(new BasicErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }*/
}
