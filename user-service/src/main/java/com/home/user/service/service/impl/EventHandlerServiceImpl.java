package com.home.user.service.service.impl;

import com.home.growme.common.module.events.*;
import com.home.user.service.config.EventMetrics;
import com.home.user.service.exception.UserAlreadyExistException;
import com.home.user.service.exception.UserNotFoundException;
import com.home.user.service.model.entity.User;
import com.home.user.service.repository.UserRepository;
import com.home.user.service.service.EventHandlerService;
import com.home.user.service.service.EventPublisherService;
import com.home.user.service.service.UserCommandService;
import com.home.user.service.util.EventValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.*;

@Slf4j
@Service
@Transactional
public class EventHandlerServiceImpl implements EventHandlerService {


    private final UserRepository userRepository;
    private final EventPublisherService eventPublisherService;
    private final EventValidator eventValidator;
    private final UserCommandService userCommandService;
    private final EventMetrics metricsService;

    public EventHandlerServiceImpl(UserRepository userRepository,
                                   EventPublisherService eventPublisherService, EventValidator eventValidator,
                                   UserCommandService userCommandService, EventMetrics metricsService) {
        this.userRepository = userRepository;
        this.eventPublisherService = eventPublisherService;
        this.eventValidator = eventValidator;
        this.userCommandService = userCommandService;
        this.metricsService = metricsService;
    }


    @Override
    @KafkaListener(topics = USER_ROLE_ASSIGNMENT_RESULT_TOPIC)
    public void handleRoleAssignmentResult(RoleAssignmentResult result) {
        try {
            eventValidator.validateRoleAssignmentResult(result);

                if (!userRepository.existsById(UUID.fromString(result.getUserId()))) {
                    User user = userRepository.findById(UUID.fromString(result.getUserId()))
                            .orElseThrow(() -> new UserNotFoundException(result.getUserId()));

                    UserCreatedEvent event = new UserCreatedEvent(
                            result.getUserId(),
                            user.getUsername(),
                            user.getEmail());

                    eventPublisherService.publishUserCreated(event);
                    metricsService.recordRoleAssignmentSuccess();
            } else {
                    throw new UserAlreadyExistException("User exist whit that Id: " + result.getUserId());
                }

        } catch (Exception e) {
            metricsService.recordRoleAssignmentFailure();
            log.error("Failed to process role assignment result", e);
            throw e;
        }
    }

    @Override
    @KafkaListener(topics = PRODUCT_ASSIGNMENT_TOPIC)
    public void handleProductAssignment(ProductAssignedToUserEvent event) {
        try {
            log.debug("Processing product assignment from event {}", event);
            eventValidator.validateProductAssignment(event);

            userCommandService.addOwnedProduct(event.getUserId(), event.getProductId());
            log.info("Successfully processed product assignment for user {}", event.getUserId());
            metricsService.recordProductAssignmentSuccess();

        } catch (Exception e) {
            log.error("Failed to process product assignment", e);
            metricsService.recordProductAssignmentFailure();
            throw e;
        }
    }

    @Override
    @KafkaListener(topics = PRODUCT_DELETION_TOPIC)
    public void handleProductDeletion(ProductDeletionToUserEvent event) {

        try {
            log.debug("Processing product deletion from event {}", event);
            eventValidator.validateProductDeletion(event);

            userCommandService.deleteOwnedProduct(event.getUserId(),event.getProductId());
            log.info("Successfully processed product deletion for user {}", event.getUserId());
            metricsService.recordProductDeletionSuccess();

        }catch (Exception e){
            log.error("Failed to process product deletion", e);
            metricsService.recordProductDeletionFailure();
            throw e;
        }
    }

    @Override
    @KafkaListener(topics = ORDER_COMPLETED_TOPIC)
    public void orderCompleted(OrderCompletedEvent event) {

        try {
            log.debug("Processing order completed from event {}", event);
            eventValidator.validateOrderCompleted(event);

            userCommandService.addOwnerOrder(event.getOrderUserId(),event.getOrderId());
            log.info("Successfully added order: {} for user {}",event.getOrderId(), event.getOrderUserId());
            metricsService.recordOrderCompletedSuccess();
        }catch (Exception e){
            log.error("Failed to process add order", e);
            metricsService.recordOrderCompletedFailure();
            throw e;
        }
    }
}
