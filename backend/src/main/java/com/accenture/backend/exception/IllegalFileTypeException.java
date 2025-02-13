package com.accenture.backend.exception;

public class IllegalFileTypeException extends RuntimeException {
    public IllegalFileTypeException(String message) {
        super(message);
    }
}
