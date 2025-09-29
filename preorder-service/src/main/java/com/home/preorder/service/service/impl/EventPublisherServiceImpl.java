package com.home.preorder.service.service.impl;

import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.preorder.service.config.EventMetrics;
import com.home.preorder.service.model.enums.EventType;
import com.home.preorder.service.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.EMAIL_SEND_TOPIC;

@Slf4j
@Service
public class EventPublisherServiceImpl implements EventPublisherService {

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY_MS = 500;
    private static final int RETRY_MULTIPLIER = 2;

    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final EventMetrics eventMetrics;

    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate, EventMetrics eventMetrics) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventMetrics = eventMetrics;
    }

    @Retryable(
            retryFor = {EventPublishingException.class, ExecutionException.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY_MS, multiplier = RETRY_MULTIPLIER)
    )
    @Override
    public void publishEmailRequest(EmailRequestEvent event) {
        System.out.println("Working well");
        try {
            kafkaTemplate.send(EMAIL_SEND_TOPIC, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish EmailRequestEvent for email={} to topic={}. Event: {}",
                                    event.getEmail(), EMAIL_SEND_TOPIC, event, ex);
                            eventMetrics.recordFailure(EventType.EMAIL_SEND);
                            throw new EventPublishingException("Critical publishing failure", ex);
                        } else {
                            log.info("Published EmailRequestEvent for email={} to topic={} at offset={}",
                                    event.getEmail(), EMAIL_SEND_TOPIC, result.getRecordMetadata().offset());
                            eventMetrics.recordSuccess(EventType.EMAIL_SEND);
                        }
                    });
        }catch (Exception e){
            log.error("Critical publish failure for email {}: {}", event.getEmail(), e.getMessage());
            eventMetrics.recordFailure(EventType.EMAIL_SEND);
        }
    }
}