package com.home.order.service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.growme.common.module.dto.UserInfo;
import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.OrderConfirmationEmailEvent;
import com.home.growme.common.module.events.PaymentIntentRequestEvent;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.order.service.config.EventMetrics;
import com.home.order.service.config.PaymentProperties;
import com.home.order.service.feign.UserServiceClient;
import com.home.order.service.model.enums.EventType;
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
    private final UserServiceClient userServiceClient;
    private final EventMetrics eventMetrics;

    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate, CorrelationService correlationService,
                                     PaymentProperties paymentProperties, ObjectMapper objectMapper,
                                     UserServiceClient userServiceClient, EventMetrics eventMetrics) {
        this.kafkaTemplate = kafkaTemplate;
        this.correlationService = correlationService;
        this.paymentProperties = paymentProperties;
        this.objectMapper = objectMapper;
        this.userServiceClient = userServiceClient;
        this.eventMetrics = eventMetrics;
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
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish OrderCompletedEvent for orderId={} to topic={}",
                                    event.getOrderId(), ORDER_COMPLETED_TOPIC, ex);
                            eventMetrics.recordFailure(EventType.ORDER_COMPLETED);
                            throw new EventPublishingException("Failed to publish order completed event", ex);
                        }

                        log.debug("Published OrderCompletedEvent for orderId={} at offset={}",
                                event.getOrderId(), result.getRecordMetadata().offset());
                        eventMetrics.recordSuccess(EventType.ORDER_COMPLETED);

                        // publish confirmation email event
                        try {
                            UserInfo userInfo = userServiceClient.getUserInfo(event.getOrderUserId());

                            OrderConfirmationEmailEvent emailEvent = new OrderConfirmationEmailEvent(
                                    event.getBuyerEmail(),
                                    event.getOrderId(),
                                    userInfo,
                                    event.getTotalAmount(),
                                    event.getItems()
                            );

                            kafkaTemplate.send(EMAIL_SEND_TOPIC, emailEvent)
                                    .whenComplete((emailResult, emailEx) -> {
                                        if (emailEx != null) {
                                            log.error("Failed to publish OrderConfirmationEmailEvent for orderId={}",
                                                    event.getOrderId(), emailEx);
                                            eventMetrics.recordFailure(EventType.EMAIL_SEND);
                                            // TODO: DLQ or retry queue
                                        } else {
                                            log.debug("Published OrderConfirmationEmailEvent for orderId={} at offset={}",
                                                    event.getOrderId(), emailResult.getRecordMetadata().offset());
                                            eventMetrics.recordSuccess(EventType.EMAIL_SEND);
                                        }
                                    });
                        } catch (Exception emailEx) {
                            log.error("Error preparing OrderConfirmationEmailEvent for orderId={}: {}",
                                    event.getOrderId(), emailEx.getMessage(), emailEx);
                            eventMetrics.recordFailure(EventType.EMAIL_SEND);
                        }
                    });
        } catch (Exception e) {
            log.error("Critical publish failure for order {}: {}", event.getOrderId(), e.getMessage());
            eventMetrics.recordFailure(EventType.ORDER_COMPLETED);
            throw new EventPublishingException("Critical publishing failure", e);
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
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish PaymentIntentRequestEvent for basket {} [Correlation: {}]",
                                    basketId, correlationId, ex);
                            correlationService.completeExceptionally(correlationId,
                                    new EventPublishingException("Failed to publish payment intent event", ex));
                        } else {
                            log.debug("Published PaymentIntentRequestEvent for basket {} [Correlation: {}] at offset={}",
                                    basketId, correlationId, result.getRecordMetadata().offset());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Serialization failed for basket {} [Correlation: {}]", basketId, correlationId, e);
            correlationService.completeExceptionally(correlationId,
                    new EventPublishingException("Failed to serialize payment intent event"));
        }

        return responseFuture;
    }


}