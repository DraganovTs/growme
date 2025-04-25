package com.home.user.service.service.impl;

import com.home.growme.common.module.events.RoleAssignmentEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.user.service.exception.EventPublishingException;
import com.home.user.service.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventPublisherServiceImpl implements EventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String ROLE_ASSIGNMENT = "user.role.assignments";
    public static final String USER_CREATE = "user.created";


    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishRoleAssignment(String userId, String role, String operationType) {

        RoleAssignmentEvent event = new RoleAssignmentEvent(userId, role.toUpperCase(),operationType);

        try {
            kafkaTemplate.send(ROLE_ASSIGNMENT, userId, event)
                    .thenAccept(result -> {
                        log.debug("Published role assignment for user {}", userId);
                        // TODO: Add success metrics
                    })
                    .exceptionally(ex -> {
                        log.error("Publish failed for event: {}", event, ex);
                        // TODO: Add error metrics
                        throw new EventPublishingException("Failed to publish role assignment");
                    });
        } catch (Exception e) {
            log.error("Critical publish failure for user {}: {}", userId, e.getMessage());
            // TODO: Add dead letter queue handling
            throw new EventPublishingException("Critical publishing failure");
        }
    }

    @Override
    public void publishProductAssignment(String userId, String productId) {

    }

    @Override
    public void publishUserCreated(UserCreatedEvent event) {
        try {
            kafkaTemplate.send(USER_CREATE, event.getUserId(), event)
                    .thenAccept(result -> {
                        log.info("Role assignment result: {}", result);
                        throw new EventPublishingException("Failed to publish user create assignment");
                    })
                    .exceptionally(ex -> {
                        log.error("Publish failed for event: {}", event, ex);
                        throw new EventPublishingException("Critical publishing failure");

                    });
        }catch (Exception e) {
            log.error("Critical publish failure for user {}: {}", event.getUserId(), e.getMessage());
        }
    }
}
