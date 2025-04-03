package com.home.user.service.publisher;

import com.home.growme.common.module.events.RoleAssignmentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class RoleEventPublisher {

    private static final String TOPIC = "user.role.assignments";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    //TODO add retry template

    public RoleEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void publishRoleAssignments(String userId, List<String> roles) {
        roles.forEach(roleName -> {
            RoleAssignmentEvent event = new RoleAssignmentEvent(userId, roleName.toUpperCase());

            try {
                kafkaTemplate.send(TOPIC, userId, event)
                        .thenAccept(result -> {
                            log.debug("Published: {}", event);
                            // TODO: Add success metrics
                        })
                        .exceptionally(ex -> {
                            log.error("Publish failed for event: {}", event, ex);
                            // TODO: Add error metrics
                            return null;
                        });
            } catch (Exception e) {
                log.error("Critical publish failure for user {}: {}", userId, e.getMessage());
                // TODO: Add dead letter queue handling
            }
        });
    }
}
