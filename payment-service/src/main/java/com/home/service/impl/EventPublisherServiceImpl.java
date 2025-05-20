package com.home.service.impl;

import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PAYMENT_INTENT_RESPONSES_TOPIC;

@Slf4j
@Service
public class EventPublisherServiceImpl implements EventPublisherService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishPaymentIntentResponse(PaymentIntentResponseEvent response) {
        try {
            kafkaTemplate.send(PAYMENT_INTENT_RESPONSES_TOPIC, response)
                    .thenAccept(result -> log.debug("Published payment response for {}", response.getPaymentIntentId()))
                    .exceptionally(ex -> {
                        log.error("Failed to publish payment response", ex);
                        throw new EventPublishingException("Failed to publish payment response", ex);
                    });
        } catch (Exception e) {
            log.error("Critical publish failure", e);
            throw new EventPublishingException("Critical publishing failure", e);
        }
    }
}
