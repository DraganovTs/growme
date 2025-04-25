package com.home.growme.produt.service.kafka.handler;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.produt.service.exception.OwnerAlreadyExistsException;
import com.home.growme.produt.service.service.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserCreatedEventHandler {

    public static final String USER_CREATE = "user.created";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OwnerService ownerService;

    public UserCreatedEventHandler(KafkaTemplate<String, String> kafkaTemplate, OwnerService ownerService) {
        this.kafkaTemplate = kafkaTemplate;
        this.ownerService = ownerService;
    }

    @KafkaListener(topics = USER_CREATE)
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        log.info("Processing user event: {}", userCreatedEvent);

        if ("CREATE".equals(userCreatedEvent.getOperationType())) {
            try {
                ownerService.createOwner(userCreatedEvent);
                log.info("Owner created for user: {}", userCreatedEvent.getUserId());
            } catch (OwnerAlreadyExistsException e) {
                log.warn("Owner already exists: {}", userCreatedEvent.getUserId());
            }
        } else {
            log.debug("Skipping non-CREATE user event: {}", userCreatedEvent);
        }
    }
}
