package com.home.order.service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.growme.common.module.events.PaymentFailureEvent;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.order.service.config.EventMetrics;
import com.home.order.service.exception.OwnerAlreadyExistsException;
import com.home.order.service.exception.PaymentFailedException;
import com.home.order.service.model.enums.EventType;
import com.home.order.service.model.enums.OrderStatus;
import com.home.order.service.service.CorrelationService;
import com.home.order.service.service.EventHandlerService;
import com.home.order.service.service.OrderService;
import com.home.order.service.service.OwnerService;
import com.home.order.service.util.EventValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.*;

@Slf4j
@Service
public class EventHandlerServiceImpl implements EventHandlerService {

    private final CorrelationService correlationService;
    private final ObjectMapper objectMapper;
    private final OwnerService ownerService;
    private final OrderService orderService;
    private final EventMetrics eventMetrics;
    private final EventValidator eventValidator;

    public EventHandlerServiceImpl(CorrelationService correlationService, ObjectMapper objectMapper,
                                   OwnerService ownerService, OrderService orderService, EventMetrics eventMetrics, EventValidator eventValidator) {
        this.correlationService = correlationService;
        this.objectMapper = objectMapper;
        this.ownerService = ownerService;
        this.orderService = orderService;
        this.eventMetrics = eventMetrics;
        this.eventValidator = eventValidator;
    }

    @Override
    @KafkaListener(topics = PAYMENT_INTENT_RESPONSES_TOPIC)
    public void handlePaymentResponse(ConsumerRecord<String, Object> record) {
        try {
            PaymentIntentResponseEvent response = objectMapper.convertValue(record.value(), PaymentIntentResponseEvent.class);

            if (response.getCorrelationId() == null) {
                eventMetrics.recordFailure(EventType.PAYMENT_INTENT_RESPONSE);
                throw new IllegalArgumentException("Missing correlation ID in payment response");
            }

            log.info("Processing payment response [Correlation: {}]", response.getCorrelationId());
            correlationService.completeResponse(response.getCorrelationId(), response);

            orderService.updateOrderStatusByPaymentIntentId(response.getPaymentIntentId(), OrderStatus.PAYMENT_RECEIVED);
            eventMetrics.recordSuccess(EventType.PAYMENT_INTENT_RESPONSE);
        } catch (Exception e) {
            log.error("Failed to process payment response [Topic: {}, Partition: {}, Offset: {}]",
                    record.topic(), record.partition(), record.offset(), e);
            eventMetrics.recordFailure(EventType.PAYMENT_INTENT_RESPONSE);
        }
    }


    @Override
    @KafkaListener(topics = PAYMENT_FAILURES_TOPIC)
    public void handlePaymentFailure(ConsumerRecord<String, String> record) {
        try {
            PaymentFailureEvent event = objectMapper.readValue(
                    record.value(),
                    PaymentFailureEvent.class
            );

            if (event.getCorrelationId() == null) {
                log.error("Null correlation ID in failure event. Topic: {}, Partition: {}, Offset: {}",
                        record.topic(), record.partition(), record.offset());
                eventMetrics.recordFailure(EventType.PAYMENT_FAILURE);
                return;
            }

            log.warn("Processing payment failure [Correlation: {}, Error: {}]",
                    event.getCorrelationId(), event.getErrorMessage());
            correlationService.completeExceptionally(
                    event.getCorrelationId(),
                    new PaymentFailedException(event.getErrorMessage())
            );


        } catch (JsonProcessingException e) {
            log.error("JSON parsing failed [Topic: {}, Partition: {}, Offset: {}] - {}",
                    record.topic(), record.partition(), record.offset(), record.value(), e);
            eventMetrics.recordFailure(EventType.PAYMENT_FAILURE);
        } catch (Exception e) {
            log.error("Unexpected error processing failure [Topic: {}, Partition: {}, Offset: {}]",
                    record.topic(), record.partition(), record.offset(), e);
            eventMetrics.recordFailure(EventType.PAYMENT_FAILURE);
        }
    }

    @Override
    @KafkaListener(topics = USER_CREATE_TOPIC)
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        try {
            eventValidator.validateUserCreatedEvent(event);
            log.info("Processing user creation event for user: {}", event.getUserId());

            ownerService.createOwner(event);
            log.info("Successfully created owner for user: {}", event.getUserId());
            eventMetrics.recordSuccess(EventType.USER_CREATED);
        } catch (OwnerAlreadyExistsException e) {
            log.warn("Owner already exists for user: {}", event.getUserId());
            eventMetrics.recordFailure(EventType.USER_CREATED);
        } catch (Exception e) {
            log.error("Failed to process user creation event for user: {}", event.getUserId(), e);
            eventMetrics.recordFailure(EventType.USER_CREATED);
            throw e;
        }
    }

}