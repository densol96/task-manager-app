package com.accenture.backend.exception.custom;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String inputName, Object value) {
        super("Invalid " + inputName + "'s value of " + value.toString());
    }

    public InvalidInputException(String message) {
        super(message);
    }
}
