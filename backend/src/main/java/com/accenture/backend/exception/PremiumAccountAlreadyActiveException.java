package com.accenture.backend.exception;

public class PremiumAccountAlreadyActiveException extends RuntimeException {
    public PremiumAccountAlreadyActiveException() {
        super("You already have an active premium account");
    }

    public PremiumAccountAlreadyActiveException(String message) {
        super(message);
    }
}
