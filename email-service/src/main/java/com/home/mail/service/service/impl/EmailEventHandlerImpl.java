package com.home.mail.service.service.impl;

import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.mail.service.exception.EmailProcessingException;
import com.home.mail.service.service.EmailEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.EMAIL_SEND_TOPIC;

@Slf4j
@Service
public class EmailEventHandlerImpl implements EmailEventHandler {



    @Override
    @KafkaListener(topics = EMAIL_SEND_TOPIC)
    public void handleEmailRequest(EmailRequestEvent event) {
        try {
            switch (event.getType()){
                case PRODUCT_ADD_CONFIRMATION:
                    log.info("PRODUCT_ADD_CONFIRMATION");
                    break;
                case ACCOUNT_DELETION_CONFIRMATION:
                    log.info("ACCOUNT_DELETION_CONFIRMATION");
                    break;
                case ACCOUNT_COMPLETION_CONFIRMATION:
                    log.info("ACCOUNT_COMPLETION_CONFIRMATION");
                    break;
                    case ACCOUNT_UPDATE_CONFIRMATION:
                    log.info("ACCOUNT_UPDATE_CONFIRMATION");
                    break;
            }
        } catch (Exception e){
            log.error("Failed to process email request for {}", event.getEmail(), e);
            throw new EmailProcessingException("Failed to process email request");
        }


    }
}
