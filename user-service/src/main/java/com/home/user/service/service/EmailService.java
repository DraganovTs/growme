package com.home.user.service.service;


public interface EmailService {

    void sendProductAddForSellConfirmation(String email);

    void sendAccountDeletionConfirmation(String email);

    void sendAccountCompletionConfirmation(String email);

    void sendAccountUpdateConfirmation(String email);
}
