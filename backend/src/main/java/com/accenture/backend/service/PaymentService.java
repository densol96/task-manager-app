package com.accenture.backend.service;

import org.springframework.http.ResponseEntity;

import com.accenture.backend.dto.response.CheckoutDto;
import com.stripe.exception.StripeException;

public interface PaymentService {
    public CheckoutDto createCheckoutSession() throws StripeException;

    public ResponseEntity<String> handleWebhook(String payload, String sigHeader);
}
