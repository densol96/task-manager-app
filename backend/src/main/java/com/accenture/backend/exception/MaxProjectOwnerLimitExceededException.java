package com.accenture.backend.exception;

public class MaxProjectOwnerLimitExceededException extends RuntimeException {

    public MaxProjectOwnerLimitExceededException(String message) {
        super(message);
    }

    public MaxProjectOwnerLimitExceededException() {
        super("Without premium account you can own up to 5 projects only!");
    }
}