package com.accenture.backend.exception.custom;

public class UserAlreadyMemberException extends RuntimeException {
    public UserAlreadyMemberException(String message) {
        super(message);
    }

    public UserAlreadyMemberException() {
        super("The user is already a member of this project.");
    }
}
