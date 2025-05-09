package com.home.service.impl;

import com.home.growme.common.module.events.PaymentIntentRequestEvent;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.service.EventHandlerService;
import com.home.service.EventPublisherService;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PAYMENT_INTENT_EVENTS;


@Slf4j
@Service
public class EventHandlerServiceImpl implements EventHandlerService {

    private final PaymentServiceImpl paymentService;
    private final EventPublisherService eventPublisherService;

    public EventHandlerServiceImpl(PaymentServiceImpl paymentService, EventPublisherService eventPublisherService) {
        this.paymentService = paymentService;
        this.eventPublisherService = eventPublisherService;
    }


    @Override
    @KafkaListener(topics = PAYMENT_INTENT_EVENTS)
    public void handleEvent(PaymentIntentRequestEvent request) {
        log.info("Payment intent created/updated successfully");

        try {
            PaymentIntentResponseEvent paymentIntent = paymentService.createOrUpdatePaymentIntent(request);
            log.info("Payment intent created/updated successfully");
            eventPublisherService.publishPaymentIntentResponse(paymentIntent);
        } catch (StripeException e) {
            log.error("StripeException occurred while handling payment intent: {}", e.getMessage(), e);
        }
    }
}
