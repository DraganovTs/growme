package com.home.service.impl;

import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.growme.common.module.dto.PaymentIntentResponse;
import com.home.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventPublisherServiceImpl implements EventPublisherService {

    private final KafkaTemplate<String,Object> kafkaTemplate;
    private static final String PAYMENT_INTENT_RESPONSE = "payment.intent.response";

    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishPaymentIntentResponse(PaymentIntentResponse response) {

        try {
            kafkaTemplate.send(PAYMENT_INTENT_RESPONSE, response)
                    .thenAccept( result -> {
                        log.debug("Published payment intent request for PaymentIntentId: {}", response.getPaymentIntentId());
                        // TODO: Add success metrics
                    })
                    .exceptionally( ex-> {
                        log.error("Publish failed for event: {}", response, ex);
                        throw new EventPublishingException("Failed to publish payment intent request");

                    });
        }catch (Exception e) {
            log.error("Critical publish failure payment intent request for PaymentIntentId {}: {}", response.getPaymentIntentId(), e.getMessage());
            // TODO: Add dead letter queue handling
            throw new EventPublishingException("Critical publishing failure");
        }
    }
}
