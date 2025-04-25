package com.home.user.service.service;

import com.home.growme.common.module.events.UserCreatedEvent;

public interface EventPublisherService {

    void publishRoleAssignment(String userId, String role,String operationType);
    void publishProductAssignment(String userId, String productId);
    void publishUserCreated(UserCreatedEvent event);
}
