package com.home.order.service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.growme.common.module.events.PaymentFailureEvent;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.order.service.exception.OwnerAlreadyExistsException;
import com.home.order.service.exception.PaymentFailedException;
import com.home.order.service.service.CorrelationService;
import com.home.order.service.service.EventHandlerService;
import com.home.order.service.service.OwnerService;
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

    public EventHandlerServiceImpl(CorrelationService correlationService, ObjectMapper objectMapper,
                                   OwnerService ownerService) {
        this.correlationService = correlationService;
        this.objectMapper = objectMapper;
        this.ownerService = ownerService;
    }

    @Override
    @KafkaListener(topics = PAYMENT_INTENT_RESPONSES_TOPIC)
    public void handlePaymentResponse(ConsumerRecord<String, Object> record) {
        try {
            PaymentIntentResponseEvent response = objectMapper.convertValue(record.value(), PaymentIntentResponseEvent.class);

            if (response.getCorrelationId() == null) {
                throw new IllegalArgumentException("Missing correlation ID in payment response");
            }

            log.info("Processing payment response [Correlation: {}]", response.getCorrelationId());
            correlationService.completeResponse(response.getCorrelationId(), response);

        } catch (Exception e) {
            log.error("Failed to process payment response [Topic: {}, Partition: {}, Offset: {}]",
                    record.topic(), record.partition(), record.offset(), e);
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
        } catch (Exception e) {
            log.error("Unexpected error processing failure [Topic: {}, Partition: {}, Offset: {}]",
                    record.topic(), record.partition(), record.offset(), e);
        }
    }

    @Override
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        try {
           //TODO add event validator
            log.info("Processing user creation event for user: {}", event.getUserId());

            ownerService.createOwner(event);
            log.info("Successfully created owner for user: {}", event.getUserId());
        } catch (OwnerAlreadyExistsException e) {
            log.warn("Owner already exists for user: {}", event.getUserId());
        }catch (Exception e){
            log.error("Failed to process user creation event for user: {}", event.getUserId(), e);
            throw e;
        }
    }

}