package com.home.order.service.service;

import com.home.growme.common.module.events.UserCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Service interface for handling various events in the system, including payment-related events,
 * user creation events, and more. This interface defines the contract for the event processing
 * logic implemented in the application.
 */
public interface EventHandlerService {

    void handlePaymentResponse(ConsumerRecord<String, Object> record);
    void handlePaymentFailure(ConsumerRecord<String, String> record);
    void handleUserCreatedEvent(UserCreatedEvent event);
}
