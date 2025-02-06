package com.accenture.backend.exception.custom;

public class InvalidInteractionTypeException extends RuntimeException {
    public InvalidInteractionTypeException(String message) {
        super(message);
    }
}
