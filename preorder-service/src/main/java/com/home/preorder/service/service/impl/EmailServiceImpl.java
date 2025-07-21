package com.home.preorder.service.service.impl;

import com.home.growme.common.module.enums.EmailType;
import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.growme.common.module.exceptions.EmailProcessingException;
import com.home.preorder.service.service.EmailService;
import com.home.preorder.service.service.EventPublisherService;
import com.home.preorder.service.service.TaskUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final EventPublisherService eventPublisherService;
    private final TaskUserService taskUserService;

    public EmailServiceImpl(EventPublisherService eventPublisherService, TaskUserService taskUserService) {
        this.eventPublisherService = eventPublisherService;
        this.taskUserService = taskUserService;
    }

    @Override
    public void sendTaskCreationConfirmation(UUID taskUserId) {
        String email = taskUserService.findUserEmailByUserId(taskUserId);
        publishEmailEvent(email, EmailType.TASK_CREATION_CONFIRMATION);

    }

    @Override
    public void sendTaskStatusUpdateNotification(UUID taskUserId) {
        String email = taskUserService.findUserEmailByUserId(taskUserId);
        publishEmailEvent(email, EmailType.TASK_STATUS_UPDATE);
    }

    @Override
    public void sendTaskCancellationConfirmation(UUID taskUserId) {
        String email = taskUserService.findUserEmailByUserId(taskUserId);
        publishEmailEvent(email, EmailType.TASK_CANCELLATION_CONFIRMATION);

    }

    @Override
    public void sendBidCreationConfirmation(UUID taskUserId) {
        String email = taskUserService.findUserEmailByUserId(taskUserId);
        publishEmailEvent(email, EmailType.BID_CREATION_CONFIRMATION);

    }

    @Override
    public void sendBidWithdrawalConfirmation(UUID taskUserId) {
        String email = taskUserService.findUserEmailByUserId(taskUserId);
        publishEmailEvent(email, EmailType.BID_WITHDRAWAL_CONFIRMATION);

    }

    @Override
    public void sendCounterOfferNotification(UUID taskUserId) {
        String email = taskUserService.findUserEmailByUserId(taskUserId);
        publishEmailEvent(email, EmailType.COUNTER_OFFER_NOTIFICATION);

    }

    @Override
    public void sendBidStatusUpdateNotification(UUID taskUserId) {
        String email = taskUserService.findUserEmailByUserId(taskUserId);
        publishEmailEvent(email, EmailType.BID_STATUS_UPDATE);

    }

    @Override
    public void sendActionRequiredNotification(UUID taskUserId) {
        String email = taskUserService.findUserEmailByUserId(taskUserId);
        publishEmailEvent(email, EmailType.BID_ACTION_REQUIRED);

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
