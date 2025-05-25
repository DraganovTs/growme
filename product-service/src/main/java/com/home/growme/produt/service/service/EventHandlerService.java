package com.home.growme.produt.service.service;

import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.UserCreatedEvent;

/**
 * Interface defining the contract for handling event processing in the system.
 * Provides methods to handle specific types of events and perform associated actions.
 */
public interface EventHandlerService {

    void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent);
    void OrderCompletedEvent(OrderCompletedEvent event);
}
