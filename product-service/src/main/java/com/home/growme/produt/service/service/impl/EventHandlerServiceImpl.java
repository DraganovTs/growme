package com.home.growme.produt.service.service.impl;

import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.produt.service.exception.OwnerAlreadyExistsException;
import com.home.growme.produt.service.service.EventHandlerService;
import com.home.growme.produt.service.service.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.ORDER_COMPLETED_TOPIC;
import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.USER_CREATE_TOPIC;


@Slf4j
@Service
public class EventHandlerServiceImpl implements EventHandlerService {

    private final OwnerService ownerService;

    public EventHandlerServiceImpl(OwnerService ownerService) {
        this.ownerService = ownerService;
    }


    @Override
    @KafkaListener(topics = USER_CREATE_TOPIC)
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        //TODO add event validator
        log.info("Processing user event: {}", userCreatedEvent);
        try {
            ownerService.createOwner(userCreatedEvent);
            log.info("Owner created for user: {}", userCreatedEvent.getUserId());
        } catch (OwnerAlreadyExistsException e) {
            log.warn("Owner already exists: {}", userCreatedEvent.getUserId());
        }

    }

    @Override
    @KafkaListener(topics = ORDER_COMPLETED_TOPIC)
    public void OrderCompletedEvent(OrderCompletedEvent event) {
        //TODO add event validator
        //TODO finish logic
        log.info("event is received");
    }
}
