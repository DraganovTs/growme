package com.home.preorder.service.service;

import com.home.growme.common.module.events.EmailRequestEvent;

public interface EventPublisherService {
    void publishEmailEvent(EmailRequestEvent event);
}
