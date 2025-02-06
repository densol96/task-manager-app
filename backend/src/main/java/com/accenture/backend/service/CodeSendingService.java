package com.accenture.backend.service;

public interface CodeSendingService {
    void sendEmail(String toEmail, String code);
}
