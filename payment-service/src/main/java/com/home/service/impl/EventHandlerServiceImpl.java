package com.home.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.growme.common.module.events.PaymentIntentRequestEvent;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.service.EventHandlerService;
import com.home.service.EventPublisherService;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PAYMENT_INTENT_REQUESTS;


@Slf4j
@Service
public class EventHandlerServiceImpl implements EventHandlerService {

    private final PaymentServiceImpl paymentService;
    private final EventPublisherService eventPublisherService;
    private final ObjectMapper objectMapper;

    public EventHandlerServiceImpl(PaymentServiceImpl paymentService, EventPublisherService eventPublisherService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.eventPublisherService = eventPublisherService;
        this.objectMapper = objectMapper;
    }

    @Override
    @KafkaListener(topics = PAYMENT_INTENT_REQUESTS)
    public void handlePaymentRequest(ConsumerRecord<String, String> record) {
        try {
            PaymentIntentRequestEvent request = objectMapper.readValue(
                    record.value(),
                    PaymentIntentRequestEvent.class
            );

            log.info("Processing payment request for basket: {}", request.getBasketId());
            PaymentIntentResponseEvent response = paymentService.createOrUpdatePaymentIntent(request);
            eventPublisherService.publishPaymentIntentResponse(response);

        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize payment request", e);
        } catch (StripeException e) {
            log.error("Payment processing failed", e);
            // Publish failure event
        }
    }
}