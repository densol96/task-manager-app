package com.accenture.backend.exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String errorCause) {
        super(errorCause);
    }
}
