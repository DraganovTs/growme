package com.home.service.impl;

import com.home.growme.common.module.dto.PaymentIntentRequest;
import com.home.service.EventHandlerService;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventHandlerServiceImpl implements EventHandlerService {

    private static final String PAYMENT_INTENT = "payment.intent";
    private final PaymentServiceImpl paymentService;

    public EventHandlerServiceImpl(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }


    @Override
    @KafkaListener(topics = PAYMENT_INTENT)
    public void handleEvent(PaymentIntentRequest request) {
        log.info("Payment intent created/updated successfully");

//        try {
//            paymentService.createOrUpdatePaymentIntent(request);
//            log.info("Payment intent created/updated successfully");
//        } catch (StripeException e) {
//            log.error("StripeException occurred while handling payment intent: {}", e.getMessage(), e);
//        }
    }
}
