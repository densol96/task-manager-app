package com.accenture.backend.exception.custom;

public class PageOutOfRangeException extends RuntimeException {
    public PageOutOfRangeException(String message) {
        super(message);
    }

    public PageOutOfRangeException(Integer pageInput, Long pagesTotal) {
        super("Page " + pageInput + " exceeds total pages (" + pagesTotal + ")");
    }
}
