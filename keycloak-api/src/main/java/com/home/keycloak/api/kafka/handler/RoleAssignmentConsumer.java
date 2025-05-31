package com.home.keycloak.api.kafka.handler;

import com.home.growme.common.module.events.RoleAssignmentEvent;
import com.home.growme.common.module.events.RoleAssignmentResult;
import com.home.keycloak.api.service.KeycloakRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.ROLE_ASSIGNMENT_TOPIC;
import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.USER_ROLE_ASSIGNMENT_RESULT_TOPIC;

@Service
@Slf4j
public class RoleAssignmentConsumer {

    private final KeycloakRoleService keycloakRoleService;
    private final KafkaTemplate<String, Object> kafkaTemplate;




    public RoleAssignmentConsumer(KeycloakRoleService keycloakRoleService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.keycloakRoleService = keycloakRoleService;
        this.kafkaTemplate = kafkaTemplate;
    }


    @KafkaListener(
            topics = ROLE_ASSIGNMENT_TOPIC

    )
    public void consumeRoleAssignment(RoleAssignmentEvent event) {
        try {
            log.info("Processing: {}", event);
            keycloakRoleService.assignRole(event.getUserId(),event.getRoleName());

            RoleAssignmentResult assignmentResult = new RoleAssignmentResult("user.role.assignments.result",
                    event.getUserId(),
                   true);
            kafkaTemplate.send(USER_ROLE_ASSIGNMENT_RESULT_TOPIC,event.getUserId(),assignmentResult)
                    .thenAccept(result-> {
                        log.debug("Role assignment: {}", result);
                        // TODO: Add success metrics
                    })
                    .exceptionally(ex -> {
                        log.error("Publish failed for event: {}", event, ex);
                        // TODO: Add error metrics
                        return null;
                    });

        } catch (Exception e) {
            log.error("Critical publish failure for user {}: {}", event.getUserId(), e.getMessage());
            // TODO: Add dead letter queue handling
        }
    }
}
