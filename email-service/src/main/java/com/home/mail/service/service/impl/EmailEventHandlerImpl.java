package com.home.mail.service.service.impl;

import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.mail.service.service.EmailEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailEventHandlerImpl implements EmailEventHandler {
    @Override
    public void handleEmailRequest(EmailRequestEvent event) {

    }
}
