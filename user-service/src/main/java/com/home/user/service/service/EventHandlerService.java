package com.home.user.service.service;

import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.growme.common.module.events.ProductDeletionToUserEvent;
import com.home.growme.common.module.events.RoleAssignmentResult;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * The EventHandlerService interface defines methods for handling various event-based actions
 * within the application. Each method corresponds to the processing of a specific type of event.
 * These events are triggered asynchronously, typically by message brokers like Kafka, and the
 * interface ensures consistent handling of these events across the system.
 */
public interface EventHandlerService {
    void handleRoleAssignmentResult(RoleAssignmentResult result);

    void handleProductAssignment(ProductAssignedToUserEvent event);

    void handleProductDeletion(ProductDeletionToUserEvent event);

    void orderCompleted(OrderCompletedEvent event);
}
