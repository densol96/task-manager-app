package com.accenture.backend.controller.advice;

import com.accenture.backend.dto.response.BasicErrorDto;
import com.accenture.backend.dto.response.ValidationErrorResponseDto;
import com.accenture.backend.exception.EmailAlreadyInUseException;
import com.accenture.backend.exception.FileUploadException;
import com.accenture.backend.exception.IllegalFileNameException;
import com.accenture.backend.exception.IllegalFileTypeException;
import com.accenture.backend.exception.MailServiceException;
import com.accenture.backend.exception.ReportNotFoundException;
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

@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorMessage> usernameNotFoundHandler() {
        ErrorMessage errorDetails = new ErrorMessage("There is no user with such email");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
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
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessage> BadCredentialsHandler() {
        ErrorMessage errorDetails = new ErrorMessage("You password is incorrect");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorMessage> handleFileUploadException() {
        ErrorMessage errorDetails = new ErrorMessage("Error while saving file to S3 bucket");
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalFileTypeException.class)
    public ResponseEntity<ErrorMessage> handleIllegalFileTypeException() {
        ErrorMessage errorDetails = new ErrorMessage("File type is not supported");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalFileNameException.class)
    public ResponseEntity<ErrorMessage> handleIllegalFileNameException() {
        ErrorMessage errorDetails = new ErrorMessage("Invalid file name or missing extension");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleReportNotFoundException() {
        ErrorMessage errorDetails = new ErrorMessage("Report not found");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MailServiceException.class)
    public ResponseEntity<ErrorMessage> handleMailServiceException() {
        ErrorMessage errorDetails = new ErrorMessage("Error while sending email");
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //should be deleted in final version
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BasicErrorDto> handleUnexpectedException(Exception e) {
        System.out.println("UNEXPECTED EXCEPTION: " + e.getClass().getSimpleName() + " --- " + e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(new BasicErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
