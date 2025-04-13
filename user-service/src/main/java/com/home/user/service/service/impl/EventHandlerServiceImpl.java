package com.home.user.service.service.impl;

import com.home.growme.common.module.events.ProductAssignedToUserEvent;
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
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
public class EventHandlerServiceImpl implements EventHandlerService {

    public static final String USER_ROLE_ASSIGNMENT_RESULT = "user.role.assignments.result";
    private static final String PRODUCT_ASSIGNMENT_TOPIC = "product.user.assignment";

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
    @KafkaListener(topics = USER_ROLE_ASSIGNMENT_RESULT)
    public void handleRoleAssignmentResult(RoleAssignmentResult result) {
        try {
            eventValidator.validateRoleAssignmentResult(result);

            User user = userRepository.findById(UUID.fromString(result.getUserId()))
                    .orElseThrow(() -> new UserNotFoundException(result.getUserId()));


            UserCreatedEvent userCreatedEvent = new UserCreatedEvent(result.getUserId(), user.getUsername());

            eventPublisherService.publishUserCreated(userCreatedEvent);

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
    public void handleProductAssignment(ProductAssignedToUserEvent event,
                                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.debug("Processing product assignment from topic {}: {}", topic, event);
            eventValidator.validateProductAssignment(event);

            userUpdateService.addOwnedProduct(event.getUserId(), event.getProductId());
            log.info("Successfully processed product assignment for user {}", event.getUserId());

        } catch (Exception e) {
            log.error("Failed to process product assignment", e);
            throw e;
        }
    }
}
