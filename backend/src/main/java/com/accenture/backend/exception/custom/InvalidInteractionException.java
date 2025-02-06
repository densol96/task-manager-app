package com.accenture.backend.exception.custom;

public class InvalidInteractionException extends RuntimeException {
    public InvalidInteractionException(String message) {
        super(message);
    }
}
