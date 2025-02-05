package com.accenture.backend.exception.custom;

public class MaxProjectOwnerLimitExceededException extends RuntimeException {

    public MaxProjectOwnerLimitExceededException(String message) {
        super(message);
    }

    public MaxProjectOwnerLimitExceededException() {
        super("You have already reached the maximum number of projects you can own.");
    }
}