package com.home.preorder.service.service;

public interface EmailService {

    void sendTaskCreationConfirmation(String email);
    void sendTaskStatusUpdateNotification(String email);
    void sendTaskCancellationConfirmation(String email);
    void sendBidCreationConfirmation(String email);
    void sendBidWithdrawalConfirmation(String email);
    void sendCounterOfferNotification(String email);
    void sendBidStatusUpdateNotification(String email);
    void sendActionRequiredNotification(String email);
}
