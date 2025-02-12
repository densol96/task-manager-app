package com.accenture.backend.service.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.accenture.backend.dto.response.CheckoutDto;
import com.accenture.backend.exception.custom.PremiumAccountAlreadyActiveException;
import com.accenture.backend.service.PaymentService;
import com.accenture.backend.service.PremiumAccountService;
import com.accenture.backend.service.UserService;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final UserService userService;
    private final PremiumAccountService premiumAccountService;

    @Value("${frontend.domain.url}")
    private String frontendUrl;

    @Value("${stripe.product.id}")
    private String premiumAccountProduct;

    @Value("${stripe.webhook.secret}")
    private String ENDPOINT_SECRET;

    @Value("${stripe.api.key}")
    private String API_KEY;

    @PostConstruct
    public void init() {
        Stripe.apiKey = API_KEY;
    }

    @Override
    public CheckoutDto createCheckoutSession() throws StripeException {
        Long loggedInUserId = userService.getLoggedInUserId();

        if (premiumAccountService.userHasActivePremiumAccount(loggedInUserId))
            throw new PremiumAccountAlreadyActiveException();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/payments/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/payments/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPrice(premiumAccountProduct)
                                .build())
                .putMetadata("userId", String.valueOf(loggedInUserId))
                .build();

        Session session = Session.create(params);
        return new CheckoutDto(session.getUrl());
    }

    @Override
    public ResponseEntity<String> handleWebhook(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, ENDPOINT_SECRET);
            if ("checkout.session.completed".equals(event.getType())) {
                EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
                Optional<StripeObject> object = deserializer.getObject();
                if (object.isPresent() && object.get() instanceof Session) {
                    Session session = (Session) object.get();
                    String userId = session.getMetadata().get("userId");
                    if (userId != null) {
                        premiumAccountService.createPremiumAccount(Long.parseLong(userId));
                        return new ResponseEntity<>("Premium Account added", HttpStatus.CREATED);
                    }
                }
            }
            log.error("Event type or data invalid");
            return new ResponseEntity<>("Event type or data invalid", HttpStatus.BAD_REQUEST);
        } catch (SignatureVerificationException e) {
            log.error("Signature verification failed: {}", e.getMessage(), e);
            return new ResponseEntity<>("Invalid signature", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return new ResponseEntity<String>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
