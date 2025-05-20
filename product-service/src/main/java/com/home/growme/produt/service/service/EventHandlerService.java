package com.home.growme.produt.service.service;

import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.UserCreatedEvent;

public interface EventHandlerService {

    void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent);
    void OrderCompletedEvent(OrderCompletedEvent event);
}
