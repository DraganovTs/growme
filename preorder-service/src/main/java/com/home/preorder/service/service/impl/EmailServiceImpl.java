package com.home.preorder.service.service.impl;

import com.home.growme.common.module.enums.EmailType;
import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.preorder.service.service.EmailService;
import com.home.preorder.service.service.EventPublisherService;
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
    public void sendTaskCreationConfirmation(String email) {
        publishEmailEvent(email, EmailType.TASK_CREATION_CONFIRMATION);

    }

    @Override
    public void sendTaskStatusUpdateNotification(String email) {
        publishEmailEvent(email, EmailType.TASK_STATUS_UPDATE);
    }

    @Override
    public void sendTaskCancellationConfirmation(String email) {
        publishEmailEvent(email, EmailType.TASK_CANCELLATION_CONFIRMATION);

    }

    @Override
    public void sendBidCreationConfirmation(String email) {
        publishEmailEvent(email, EmailType.BID_CREATION_CONFIRMATION);

    }

    @Override
    public void sendBidWithdrawalConfirmation(String email) {
        publishEmailEvent(email, EmailType.BID_WITHDRAWAL_CONFIRMATION);

    }

    @Override
    public void sendCounterOfferNotification(String email) {
        publishEmailEvent(email, EmailType.COUNTER_OFFER_NOTIFICATION);

    }

    @Override
    public void sendBidStatusUpdateNotification(String email) {
        publishEmailEvent(email, EmailType.BID_STATUS_UPDATE);

    }

    @Override
    public void sendActionRequiredNotification(String email) {
        publishEmailEvent(email, EmailType.BID_ACTION_REQUIRED);

    }


    private void publishEmailEvent(String email, EmailType type) {
        try {
            EmailRequestEvent event = new EmailRequestEvent(email, type);
//            eventPublisherService.publishEmailEvent(event);
            log.debug("Email request published for {}: {}", email, type);
        } catch (Exception e) {
            log.error("Failed to publish email request for {}: {}", email, type, e);
//            throw new EmailProcessingException("Failed to process email request");
        }
    }
}
