package com.home.user.service.kafka.handler;

import com.home.growme.common.module.events.RoleAssignmentResult;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.user.service.exception.UserNotFoundException;
import com.home.user.service.model.entity.User;
import com.home.user.service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
public class RoleAssignmentResultHandler {


    public static final String USER_ROLE_ASSIGNMENT_RESULT = "user.role.assignments.result";
    public static final String USER_CREATE = "user.created";
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RoleAssignmentResultHandler(UserRepository userRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = USER_ROLE_ASSIGNMENT_RESULT)
    public void handleSuccess(RoleAssignmentResult roleResult) {
        try {
            User user = userRepository.findById(UUID.fromString(roleResult.getUserId()))
                    .orElseThrow(() -> new UserNotFoundException(roleResult.getUserId()));
            UserCreatedEvent userCreatedEvent = new UserCreatedEvent(roleResult.getUserId(), user.getUsername());


            kafkaTemplate.send(USER_CREATE, userCreatedEvent)
                    .thenAccept(result -> log.info("Role assignment result: {}", result))
                    .exceptionally(ex -> {
                        log.error("Publish failed for event: {}", roleResult, ex);
                        return null;
                    });
        } catch (Exception e) {
            log.error("Critical publish failure for user {}: {}", roleResult.getUserId(), e.getMessage());
        }


    }
}
