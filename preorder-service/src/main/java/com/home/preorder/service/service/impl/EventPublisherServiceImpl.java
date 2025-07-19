package com.home.preorder.service.service.impl;

import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.preorder.service.service.EventPublisherService;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherServiceImpl implements EventPublisherService {


    @Override
    public void publishEmailEvent(EmailRequestEvent event) {
        System.out.println("Working well");
    }
}
