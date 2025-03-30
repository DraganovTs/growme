package com.home.keycloak.api.service.consumer;

import com.home.growme.common.module.dto.RoleAssignmentMessage;
import com.home.keycloak.api.service.KeycloakRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleAssigmentConsumer {

    private final KeycloakRoleService keycloakRoleService;

    public RoleAssigmentConsumer(KeycloakRoleService keycloakRoleService) {
        this.keycloakRoleService = keycloakRoleService;
    }


    @KafkaListener(
            topics = "${kafka.topics.role-assignment}",
            containerFactory = "roleAssignmentListenerFactory"
    )
    public void consumeRoleAssignment(RoleAssignmentMessage message) {
        try {
            log.info("Received role assignment request for user {} to role {}",
                    message.userId(), message.roleName());

            keycloakRoleService.assignRole(message.userId(), message.roleName());


            log.info("Successfully assigned role {} to user {}",
                    message.roleName(), message.userId());
        } catch (Exception e) {
            log.error("Failed to assign role {} to user {}: {}",
                    message.roleName(), message.userId(), e.getMessage());
            throw e;
        }
    }
}
