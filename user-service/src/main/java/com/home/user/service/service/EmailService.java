package com.home.user.service.service;

import com.home.user.service.model.dto.UserDTO;

public interface EmailService {

    void sendProductAddForSellConfirmation(String userEmail);

    void sendAccountDeletionConfirmation(String email);

    void sendAccountCompletionConfirmation(String email);

    void sendAccountUpdateConfirmation(String email);
}
