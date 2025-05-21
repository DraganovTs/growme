package com.home.growme.produt.service.service.impl;

import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.produt.service.exception.OwnerAlreadyExistsException;
import com.home.growme.produt.service.service.EventHandlerService;
import com.home.growme.produt.service.service.OwnerService;
import com.home.growme.produt.service.util.EventValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.ORDER_COMPLETED_TOPIC;
import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.USER_CREATE_TOPIC;


@Slf4j
@Service
public class EventHandlerServiceImpl implements EventHandlerService {

    private final OwnerService ownerService;
    private final EventValidator eventValidator;

    public EventHandlerServiceImpl(OwnerService ownerService, EventValidator eventValidator) {
        this.ownerService = ownerService;
        this.eventValidator = eventValidator;
    }


    @Override
    @KafkaListener(topics = USER_CREATE_TOPIC)
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        eventValidator.validateUserCreatedEvent(event);
        log.info("Processing user event: {}", event);
        try {
            ownerService.createOwner(event);
            log.info("Owner created for user: {}", event.getUserId());
        } catch (OwnerAlreadyExistsException e) {
            log.warn("Owner already exists: {}", event.getUserId());
        }

    }

    @Override
    @KafkaListener(topics = ORDER_COMPLETED_TOPIC)
    public void OrderCompletedEvent(OrderCompletedEvent event) {
        eventValidator.validateOrderCompletedEvent(event);
        //TODO finish logic
        log.info("event is received");
    }
}
