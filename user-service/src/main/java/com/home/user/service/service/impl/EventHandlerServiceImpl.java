package com.home.user.service.service.impl;

import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.growme.common.module.events.ProductDeletionToUserEvent;
import com.home.growme.common.module.events.RoleAssignmentResult;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.user.service.exception.UserNotFoundException;
import com.home.user.service.model.entity.User;
import com.home.user.service.repository.UserRepository;
import com.home.user.service.service.EventHandlerService;
import com.home.user.service.service.EventPublisherService;
import com.home.user.service.service.UserUpdateService;
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
    private final UserUpdateService userUpdateService;

    public EventHandlerServiceImpl( UserRepository userRepository,
                                   EventPublisherService eventPublisherService, EventValidator eventValidator,
                                   UserUpdateService userUpdateService) {
        this.userRepository = userRepository;
        this.eventPublisherService = eventPublisherService;
        this.eventValidator = eventValidator;
        this.userUpdateService = userUpdateService;
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
                            user.getUsername());

                    eventPublisherService.publishUserCreated(event);
            }

        } catch (UserNotFoundException e) {
            log.error("User not found for role assignment: {}", result.getUserId(), e);
            throw e;
        } catch (Exception e) {
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

            userUpdateService.addOwnedProduct(event.getUserId(), event.getProductId());
            log.info("Successfully processed product assignment for user {}", event.getUserId());

        } catch (Exception e) {
            log.error("Failed to process product assignment", e);
            throw e;
        }
    }

    @Override
    @KafkaListener(topics = PRODUCT_DELETION_TOPIC)
    public void handleProductDeletion(ProductDeletionToUserEvent event) {

        try {
            log.debug("Processing product deletion from event {}", event);
            eventValidator.validateProductDeletion(event);

            userUpdateService.deleteOwnedProduct(event.getUserId(),event.getProductId());
            log.info("Successfully processed product deletion for user {}", event.getUserId());

        }catch (Exception e){
            log.error("Failed to process product deletion", e);
            throw e;
        }
    }
}
