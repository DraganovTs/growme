package com.home.order.service.service.impl;

import com.home.growme.common.module.dto.PaymentIntentRequest;
import com.home.growme.common.module.dto.PaymentIntentResponse;
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


    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate, CorrelationService correlationService,
                                     PaymentProperties paymentProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.correlationService = correlationService;
        this.paymentProperties = paymentProperties;
    }


    @Override
    public CompletableFuture<PaymentIntentResponse> sendCreatePaymentIntent(String basketId, BigDecimal amount) {
        return sendPaymentIntentEvent(basketId, amount, true);    }

    @Override
    public CompletableFuture<PaymentIntentResponse> sendUpdatePaymentIntent(String basketId, BigDecimal amount) {
        return sendPaymentIntentEvent(basketId, amount, false);
    }

    private CompletableFuture<PaymentIntentResponse> sendPaymentIntentEvent(String basketId, BigDecimal amount,
                                                                            boolean isCreate) {

        String correlationId = correlationService.createCorrelation(basketId);
        String operationType = isCreate ? "create" : "update";

        PaymentIntentRequest request = PaymentIntentRequest.builder()
                .basketId(basketId)
                .amount(amount)
                .correlationId(correlationId)
                .isCreateOperation(isCreate)
                .build();

        CompletableFuture<PaymentIntentResponse> responseFuture = correlationService.awaitResponse(correlationId)
                .orTimeout(paymentProperties.getResponseTimeout().toMillis(), TimeUnit.MILLISECONDS);

        String topic = isCreate ? PAYMENT_INTENT_REQUEST : PAYMENT_INTENT_UPDATE;

        kafkaTemplate.send(topic, request)
                .thenAccept(result -> {
                    log.debug("Payment intent {} published for basketId: {}", operationType, basketId);
                    //TODO add metrics
                })
                .exceptionally(ex -> {
                    log.error("Failed to publish payment intent {} for basket: {}", operationType, basketId, ex);
                    correlationService.completeExceptionally(correlationId,
                            new EventPublishingException("Failed to publish payment intent event"));
                    //TODO add metrics
                    return null;
                });

        return responseFuture;
    }


    @Override
    public CompletableFuture<Void> publishPaymentIntentResponse(PaymentIntentResponse response) {
        return kafkaTemplate.send(PAYMENT_INTENT_RESPONSE, response)
                .thenAccept(result -> {
                    log.debug("Published payment intent response for PaymentIntentId: {}", response.getPaymentIntentId());
                    //TODO add metrics
                })
                .exceptionally(ex -> {
                    log.error("Publish failed for payment intent response: {}", response.getPaymentIntentId(), ex);
                    //TODO add metrics
                    throw new EventPublishingException("Failed to publish payment intent response", ex);
                });
    }
}