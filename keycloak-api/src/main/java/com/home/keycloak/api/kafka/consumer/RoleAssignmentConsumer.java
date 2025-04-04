package com.home.keycloak.api.kafka.consumer;

import com.home.growme.common.module.events.RoleAssignmentEvent;
import com.home.growme.common.module.events.RoleAssignmentResult;
import com.home.keycloak.api.service.KeycloakRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleAssignmentConsumer {

    private final KeycloakRoleService keycloakRoleService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.role-assignment}")
    public static final String ROLE_ASSIGNMENT = "user.role.assignments";
    @Value("${kafka.topics.role-assignment-result}")
    public static final String USER_ROLE_ASSIGNMENT_RESULT = "user.role.assignments.result";

    public RoleAssignmentConsumer(KeycloakRoleService keycloakRoleService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.keycloakRoleService = keycloakRoleService;
        this.kafkaTemplate = kafkaTemplate;
    }


    @KafkaListener(
            topics = ROLE_ASSIGNMENT,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeRoleAssignment(RoleAssignmentEvent event) {
        try {
            log.info("Processing: {}", event);
            keycloakRoleService.assignRole(event.getUserId(),event.getRoleName());

            RoleAssignmentResult assignmentResult = new RoleAssignmentResult("user.role.assignments.result",
                    event.getUserId(),
                   true);
            kafkaTemplate.send("user.role.assignments.result",event.getUserId(),assignmentResult)
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
