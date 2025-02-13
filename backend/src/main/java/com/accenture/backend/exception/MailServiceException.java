package com.accenture.backend.exception;

public class MailServiceException extends RuntimeException{
    public MailServiceException(String message) {
        super(message);
    }
}
