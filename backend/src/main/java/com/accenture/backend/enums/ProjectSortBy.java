package com.accenture.backend.enums;

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
