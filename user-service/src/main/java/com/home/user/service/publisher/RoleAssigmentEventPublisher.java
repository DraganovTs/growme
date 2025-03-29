package com.home.user.service.publisher;

import com.home.growme.common.module.dto.RoleAssignmentMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
public class RoleAssigmentEventPublisher {

    private static final String TOPIC = "user.role.assignments";
    private final KafkaTemplate<String, RoleAssignmentMessage> kafkaTemplate;

    public RoleAssigmentEventPublisher(KafkaTemplate<String, RoleAssignmentMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishRoleAssignment(String userId, String roleName) {
        RoleAssignmentMessage message = new RoleAssignmentMessage(userId, roleName);

        CompletableFuture<SendResult<String, RoleAssignmentMessage>> future =
                kafkaTemplate.send(TOPIC, userId, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Sent role assignment message=[{}] with offset=[{}]",
                        message, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send role assignment message=[{}] due to: {}",
                        message, ex.getMessage());
            }
        });
    }
}
