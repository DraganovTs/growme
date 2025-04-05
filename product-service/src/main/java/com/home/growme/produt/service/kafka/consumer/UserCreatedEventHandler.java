package com.home.growme.produt.service.kafka.consumer;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.produt.service.service.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserCreatedEventHandler {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OwnerService ownerService;

    public UserCreatedEventHandler(KafkaTemplate<String, String> kafkaTemplate, OwnerService ownerService) {
        this.kafkaTemplate = kafkaTemplate;
        this.ownerService = ownerService;
    }

    @KafkaListener(topics = "user.created")
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        log.info("User created event: {}", userCreatedEvent);
        ownerService.createOwner(userCreatedEvent);
        log.info("User whit id created successfully in product service: {}", userCreatedEvent.getUserId());
    }
}
