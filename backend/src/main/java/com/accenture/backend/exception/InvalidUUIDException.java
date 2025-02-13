package com.accenture.backend.exception;

public class InvalidUUIDException extends RuntimeException {
    public InvalidUUIDException() {
        super("Token not found or expired");
    }

    public InvalidUUIDException(String message) {
        super(message);
    }
}
