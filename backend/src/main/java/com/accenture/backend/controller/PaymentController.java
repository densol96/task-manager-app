package com.accenture.backend.controller;

import com.accenture.backend.dto.response.CheckoutDto;
import com.accenture.backend.service.PaymentService;

import com.stripe.exception.StripeException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<CheckoutDto> createCheckoutSession() throws StripeException {
        return new ResponseEntity<>(paymentService.createCheckoutSession(), HttpStatus.CREATED);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        return paymentService.handleWebhook(payload, sigHeader);
    }
}
