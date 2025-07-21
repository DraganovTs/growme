package com.home.preorder.service.service;

import java.util.UUID;

public interface EmailService {

    void sendTaskCreationConfirmation(UUID taskUserId);
    void sendTaskStatusUpdateNotification(UUID taskUserId);
    void sendTaskCancellationConfirmation(UUID taskUserId);
    void sendBidCreationConfirmation(UUID taskUserId);
    void sendBidWithdrawalConfirmation(UUID taskUserId);
    void sendCounterOfferNotification(UUID taskUserId);
    void sendBidStatusUpdateNotification(UUID taskUserId);
    void sendActionRequiredNotification(UUID taskUserId);
}
