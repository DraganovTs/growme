package com.home.user.service.service.impl;

import com.home.growme.common.module.enums.EmailType;
import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.user.service.exception.EmailProcessingException;
import com.home.user.service.service.EmailService;
import com.home.user.service.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final EventPublisherService eventPublisherService;

    public EmailServiceImpl(EventPublisherService eventPublisherService) {
        this.eventPublisherService = eventPublisherService;
    }

    @Override
    public void sendProductAddForSellConfirmation(String email) {
        publishEmailEvent(email, EmailType.PRODUCT_ADD_CONFIRMATION);
    }

    @Override
    public void sendAccountDeletionConfirmation(String email) {
        publishEmailEvent(email, EmailType.ACCOUNT_DELETION_CONFIRMATION);
    }

    @Override
    public void sendAccountCompletionConfirmation(String email) {
        publishEmailEvent(email, EmailType.ACCOUNT_COMPLETION_CONFIRMATION);
    }

    @Override
    public void sendAccountUpdateConfirmation(String email) {
        publishEmailEvent(email, EmailType.ACCOUNT_UPDATE_CONFIRMATION);
    }

    private void publishEmailEvent(String email, EmailType type) {
        try {
            EmailRequestEvent event = new EmailRequestEvent(email, type);
            eventPublisherService.publishEmailRequest(event);
            log.debug("Email request published for {}: {}", email, type);
        } catch (Exception e) {
            log.error("Failed to publish email request for {}: {}", email, type, e);
            throw new EmailProcessingException("Failed to process email request");
        }
    }

}
