package com.home.mail.service;

public interface EmailSenderService {
    void sendProductAddConfirmation(String email);
    void sendAccountDeletionConfirmation(String email);
    void sendAccountCompletionConfirmation(String email);
    void sendAccountUpdateConfirmation(String email);
}
