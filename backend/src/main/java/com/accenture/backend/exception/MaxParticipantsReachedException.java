package com.accenture.backend.exception;

public class MaxParticipantsReachedException extends RuntimeException {
    public MaxParticipantsReachedException(String message) {
        super(message);
    }

    public MaxParticipantsReachedException() {
        super("Cannot add more participants. Maximum limit reached.");
    }
}
