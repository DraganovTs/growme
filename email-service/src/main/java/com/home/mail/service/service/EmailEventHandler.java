package com.home.mail.service.service;

import com.home.growme.common.module.events.EmailRequestEvent;




public interface EmailEventHandler {

     void handleEmailRequest(EmailRequestEvent event);
}
