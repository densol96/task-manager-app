package com.accenture.backend.service;


public interface CodeVerificationService {
    void storeCode(String email, String code);
    boolean verifyCode(String email, String code);
}
