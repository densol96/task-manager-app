package com.accenture.backend.exception;

public class AuthenticationRuntimeException extends RuntimeException {

    public AuthenticationRuntimeException() {
        super("User not authenticated");
    }

    public AuthenticationRuntimeException(String message) {
        super(message);
    }
}
