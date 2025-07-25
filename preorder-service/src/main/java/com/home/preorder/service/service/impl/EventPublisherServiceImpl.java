package com.home.preorder.service.service.impl;

import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.preorder.service.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.EMAIL_SEND_TOPIC;

@Slf4j
@Service
public class EventPublisherServiceImpl implements EventPublisherService {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishEmailRequest(EmailRequestEvent event) {
        System.out.println("Working well");
        try {
            kafkaTemplate.send(EMAIL_SEND_TOPIC , event)
                    .thenAccept( result -> {
                        log.info("Mail send result: {}" , result);
                    })
                    .exceptionally( ex -> {
                        log.error("Published failed for event: {}", event ,ex);
                        throw  new EventPublishingException("Critical publishing failure");
                    });
        }catch (Exception e){
            log.error("Critical publish failure for email {}: {}", event.getEmail(), e.getMessage());
        }
    }
}