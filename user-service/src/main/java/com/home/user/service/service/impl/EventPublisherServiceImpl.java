package com.home.user.service.service.impl;

import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.growme.common.module.events.RoleAssignmentEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.user.service.config.EventMetrics;
import com.home.user.service.model.enums.EventType;
import com.home.user.service.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.*;

@Slf4j
@Service
public class EventPublisherServiceImpl implements EventPublisherService {

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY_MS = 500;
    private static final int RETRY_MULTIPLIER = 2;


    private final KafkaTemplate<String, Object> kafkaTemplate;
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
    public void publishRoleAssignment(String userId, String role) {
        RoleAssignmentEvent event = new RoleAssignmentEvent(userId, role);

        try {
            kafkaTemplate.send(ROLE_ASSIGNMENT_TOPIC,userId,event)
                    .whenComplete((result,ex)->{
                        if (ex!=null){
                            log.error("Failed to publish RoleAssignmentEvent for userId={} to topic={}. Event: {}",
                                    userId, ROLE_ASSIGNMENT_TOPIC, event, ex);
                            eventMetrics.recordFailure(EventType.ROLE_ASSIGNMENT);
                        }else {
                            log.info("✅ Published RoleAssignmentEvent for userId={} to topic={} at offset={}",
                                    userId, ROLE_ASSIGNMENT_TOPIC, result.getRecordMetadata().offset());
                            eventMetrics.recordSuccess(EventType.ROLE_ASSIGNMENT);
                        }
                    });
        } catch (Exception e) {
            log.error("Critical publish failure for user {}: {}", userId, e.getMessage());
            eventMetrics.recordFailure(EventType.ROLE_ASSIGNMENT);
            throw new EventPublishingException("Failed to publish role assignment event", e);
        }
    }

    @Retryable(
            retryFor = {EventPublishingException.class, ExecutionException.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY_MS, multiplier = RETRY_MULTIPLIER)
    )
    @Override
    public void publishUserCreated(UserCreatedEvent event) {
        try {
            kafkaTemplate.send(USER_CREATE_TOPIC, event.getUserId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish UserCreatedEvent for userId={} to topic={}. Event: {}",
                                    event.getUserId(), USER_CREATE_TOPIC, event, ex);
                            eventMetrics.recordFailure(EventType.USER_CREATED);
                        } else {
                            log.info("Published UserCreatedEvent for userId={} to topic={} at offset={}",
                                    event.getUserId(), USER_CREATE_TOPIC, result.getRecordMetadata().offset());
                            eventMetrics.recordSuccess(EventType.USER_CREATED);
                        }
                    });
        } catch (Exception e) {
            log.error("Critical publish failure for user {}: {}", event.getUserId(), e.getMessage());
            eventMetrics.recordFailure(EventType.USER_CREATED);
        }
    }

    @Retryable(
            retryFor = {EventPublishingException.class, ExecutionException.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY_MS, multiplier = RETRY_MULTIPLIER)
    )
    @Override
    public void publishEmailRequest(EmailRequestEvent event) {
        try {
            kafkaTemplate.send(EMAIL_SEND_TOPIC, event)
                    .whenComplete((result,ex)->{
                        if (ex != null) {
                            log.error("Failed to publish EmailRequestEvent for email={} to topic={}. Event: {}",
                                    event.getEmail(), EMAIL_SEND_TOPIC, event, ex);
                            eventMetrics.recordFailure(EventType.EMAIL_SEND);
                        } else {
                            log.info("Published EmailRequestEvent for email={} to topic={} at offset={}",
                                    event.getEmail(), EMAIL_SEND_TOPIC, result.getRecordMetadata().offset());
                            eventMetrics.recordSuccess(EventType.EMAIL_SEND);
                        }
                    });
        } catch (Exception e) {
            log.error("Critical publish failure for email {}: {}", event.getEmail(), e.getMessage());
            eventMetrics.recordFailure(EventType.EMAIL_SEND);
            throw new EventPublishingException("Failed to publish email request event", e);
        }
    }
}