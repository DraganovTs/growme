package com.home.preorder.service.service;

import com.home.growme.common.module.events.UserCreatedEvent;

public interface EventHandlerService {
    void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent);
}
