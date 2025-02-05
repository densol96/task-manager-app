package com.accenture.backend.exception.custom;

public class MaxParticipantsReachedException extends RuntimeException {
    public MaxParticipantsReachedException(String message) {
        super(message);
    }

    public MaxParticipantsReachedException() {
        super("Cannot add more participants. Maximum limit reached.");
    }
}
