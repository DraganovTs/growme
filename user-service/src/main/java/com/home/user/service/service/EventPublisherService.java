package com.home.user.service.service;

import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.growme.common.module.events.UserCreatedEvent;

/**
 * The EventPublisherService interface is responsible for publishing various types of events
 * within the application. These events are typically sent to an external system or message
 * broker (e.g., Kafka) for asynchronous processing.
 */
public interface EventPublisherService {

    void publishRoleAssignment(String userId, String role,String operationType);
    void publishUserCreated(UserCreatedEvent event);

    void publishEmailRequest(EmailRequestEvent event);
}
