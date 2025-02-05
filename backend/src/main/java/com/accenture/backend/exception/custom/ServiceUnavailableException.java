package com.accenture.backend.exception.custom;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String errorCause) {
        super(errorCause);
    }
}
