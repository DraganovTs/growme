package com.home.user.service.service;

import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.growme.common.module.events.UserCreatedEvent;

public interface EventPublisherService {

    void publishRoleAssignment(String userId, String role,String operationType);
    void publishUserCreated(UserCreatedEvent event);

    void publishEmailRequest(EmailRequestEvent event);
}
