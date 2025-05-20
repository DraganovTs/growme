package com.home.growme.produt.service.kafka.handler;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.produt.service.exception.OwnerAlreadyExistsException;
import com.home.growme.produt.service.service.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.USER_CREATE_TOPIC;

@Slf4j
@Service
public class UserCreatedEventHandler {

    private final OwnerService ownerService;

    public UserCreatedEventHandler(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @KafkaListener(topics = USER_CREATE_TOPIC)
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        log.info("Processing user event: {}", userCreatedEvent);
            try {
                ownerService.createOwner(userCreatedEvent);
                log.info("Owner created for user: {}", userCreatedEvent.getUserId());
            } catch (OwnerAlreadyExistsException e) {
                log.warn("Owner already exists: {}", userCreatedEvent.getUserId());
            }
    }
}
