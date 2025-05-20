package com.home.order.service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.PaymentIntentRequestEvent;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.order.service.config.PaymentProperties;
import com.home.order.service.service.CorrelationService;
import com.home.order.service.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.*;


@Slf4j
@Service
public class EventPublisherServiceImpl implements EventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CorrelationService correlationService;
    private final PaymentProperties paymentProperties;
    private final ObjectMapper objectMapper;

    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate, CorrelationService correlationService, PaymentProperties paymentProperties, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.correlationService = correlationService;
        this.paymentProperties = paymentProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public CompletableFuture<PaymentIntentResponseEvent> sendCreatePaymentIntent(String basketId, BigDecimal amount) {
        return sendPaymentIntentEvent(basketId, amount, true);
    }

    @Override
    public CompletableFuture<PaymentIntentResponseEvent> sendUpdatePaymentIntent(String basketId, BigDecimal amount) {
        return sendPaymentIntentEvent(basketId, amount, false);
    }

    @Override
    public void publishCompletedOrder(OrderCompletedEvent event) {
        try {
            kafkaTemplate.send(ORDER_COMPLETED_TOPIC, event)
                    .thenAccept(result -> {
                        log.debug("Published order completed event for order {}", event.getOrderId());
                        // TODO: Add success metrics
                    }).exceptionally(ex -> {
                        log.error("Publish failed for event: {}", event, ex);
                        // TODO: Add error metrics
                        throw new EventPublishingException("Failed to publish role assignment");

                    });
        } catch (Exception e) {
            log.error("Critical publish failure for order {}: {}", event.getOrderId(), e.getMessage());
            // TODO: Add dead letter queue handling
            throw new EventPublishingException("Critical publishing failure");

        }
    }

    private CompletableFuture<PaymentIntentResponseEvent> sendPaymentIntentEvent(
            String basketId, BigDecimal amount, boolean isCreate) {

        String correlationId = correlationService.createCorrelation(basketId);
        PaymentIntentRequestEvent event = new PaymentIntentRequestEvent(
                correlationId,
                basketId,
                amount,
                isCreate,
                isCreate ? null : correlationService.getPaymentIntentId(basketId)
        );

        CompletableFuture<PaymentIntentResponseEvent> responseFuture = correlationService
                .awaitResponse(correlationId)
                .orTimeout(paymentProperties.getResponseTimeout().toMillis(), TimeUnit.MILLISECONDS);

        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(PAYMENT_INTENT_REQUESTS_TOPIC, payload)
                    .thenAccept(result -> log.debug("Published payment intent for basket {} [Correlation: {}]",
                            basketId, correlationId))
                    .exceptionally(ex -> {
                        log.error("Publish failed for basket {} [Correlation: {}]", basketId, correlationId, ex);
                        correlationService.completeExceptionally(correlationId,
                                new EventPublishingException("Failed to publish payment intent event"));
                        return null;
                    });
        } catch (JsonProcessingException e) {
            log.error("Serialization failed for basket {} [Correlation: {}]", basketId, correlationId, e);
            correlationService.completeExceptionally(correlationId,
                    new EventPublishingException("Failed to serialize payment intent event"));
        }

        return responseFuture;
    }


}