package com.home.user.service.service.impl;

import com.home.user.service.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendProductAddForSellConfirmation(String userEmail) {
        log.info("sendProductAddForSellConfirmation");
    }

    @Override
    public void sendAccountDeletionConfirmation(String email) {
        log.info("sendAccountDeletionConfirmation");
    }

    @Override
    public void sendAccountCompletionConfirmation(String email) {
        log.info("sendAccountCompletionConfirmation");
    }

    @Override
    public void sendAccountUpdateConfirmation(String email) {
        log.info("sendAccountUpdateConfirmation");
    }
}
