package com.accenture.backend.model;

public enum ProjectSortBy {
    TITLE("title"), CREATEDAT("createdAt");

    private String fieldName;

    private ProjectSortBy(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
