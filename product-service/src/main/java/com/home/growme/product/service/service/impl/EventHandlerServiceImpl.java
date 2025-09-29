package com.home.growme.product.service.service.impl;

import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.product.service.config.EventMetrics;
import com.home.growme.product.service.exception.OwnerAlreadyExistsException;
import com.home.growme.product.service.model.enums.EventType;
import com.home.growme.product.service.service.EventHandlerService;
import com.home.growme.product.service.service.OwnerService;
import com.home.growme.product.service.service.ProductService;
import com.home.growme.product.service.util.EventValidator;
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

    private final EventMetrics eventMetrics;

    public EventHandlerServiceImpl(OwnerService ownerService, EventValidator eventValidator, ProductService productService, EventMetrics eventMetrics) {
        this.ownerService = ownerService;
        this.eventValidator = eventValidator;
        this.productService = productService;
        this.eventMetrics = eventMetrics;
    }


    @Override
    @KafkaListener(topics = USER_CREATE_TOPIC, groupId = "product-service")
    @Transactional
    public void handleUserCreatedEvent(UserCreatedEvent event) {


        try {
            eventValidator.validateUserCreatedEvent(event);
            log.info("Processing user creation event for user: {}", event.getUserId());

            if (ownerService.existsByUserId(event.getUserId())) {
                log.warn("Skipping owner creation, already exists for userId={}", event.getUserId());
                eventMetrics.recordFailure(EventType.USER_CREATED);
                return;
            }

            ownerService.createOwner(event);
            log.info("Successfully created owner for user: {}", event.getUserId());
            eventMetrics.recordSuccess(EventType.USER_CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UserCreatedEvent received for userId={}: {}", event.getUserId(), e.getMessage());
            eventMetrics.recordFailure(EventType.USER_CREATED);
        } catch (OwnerAlreadyExistsException e){
            log.warn("Owner already exists for user: {}", event.getUserId());
            eventMetrics.recordFailure(EventType.USER_CREATED);

        }catch (Exception e) {
            log.error("Failed to process user creation event for user: {}", event.getUserId(), e);
            eventMetrics.recordFailure(EventType.USER_CREATED);
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
            eventMetrics.recordSuccess(EventType.ORDER_COMPLETED);

        } catch (IllegalArgumentException e) {
            log.error("Invalid OrderCompletedEvent received for orderId={}: {}", event.getOrderId(), e.getMessage());
            eventMetrics.recordFailure(EventType.ORDER_COMPLETED);
        } catch (Exception e) {
            log.error("Failed to process order completion event for order: {}", event.getOrderId(), e);
            eventMetrics.recordFailure(EventType.ORDER_COMPLETED);
            throw e;

        }
    }
}
