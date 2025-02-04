package com.accenture.backend.exception.custom;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entity, Long id) {
        super(entity + " with ID " + id + " not found");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
