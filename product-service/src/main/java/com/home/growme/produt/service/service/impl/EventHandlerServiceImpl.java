package com.home.growme.produt.service.service.impl;

import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.produt.service.exception.OwnerAlreadyExistsException;
import com.home.growme.produt.service.service.EventHandlerService;
import com.home.growme.produt.service.service.OwnerService;
import com.home.growme.produt.service.service.ProductService;
import com.home.growme.produt.service.util.EventValidator;
import jakarta.transaction.Transactional;
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
    private final ProductService productService;

    public EventHandlerServiceImpl(OwnerService ownerService, EventValidator eventValidator, ProductService productService) {
        this.ownerService = ownerService;
        this.eventValidator = eventValidator;
        this.productService = productService;
    }


    @Override
    @KafkaListener(topics = USER_CREATE_TOPIC)
    @Transactional
    public void handleUserCreatedEvent(UserCreatedEvent event) {

        try {
            eventValidator.validateUserCreatedEvent(event);
            log.info("Processing user creation event for user: {}", event.getUserId());

            ownerService.createOwner(event);
            log.info("Successfully created owner for user: {}", event.getUserId());
        } catch (OwnerAlreadyExistsException e) {
            log.warn("Owner already exists for user: {}", event.getUserId());
        }catch (Exception e){
            log.error("Failed to process user creation event for user: {}", event.getUserId(), e);
            throw e;
        }

    }

    @Override
    @KafkaListener(topics = ORDER_COMPLETED_TOPIC)
    @Transactional
    public void OrderCompletedEvent(OrderCompletedEvent event) {

        try {
            eventValidator.validateOrderCompletedEvent(event);
            log.info("Processing order completion event for order: {}", event.getOrderId());

            productService.completeOrder(event);
            log.info("Successfully processed order: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("Failed to process order completion event for order: {}", event.getOrderId(), e);
            throw e;
        }

    }
}
