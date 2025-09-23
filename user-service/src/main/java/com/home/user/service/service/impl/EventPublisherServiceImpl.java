package com.home.user.service.service.impl;

import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.growme.common.module.events.RoleAssignmentEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.user.service.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.*;

@Slf4j
@Service
public class EventPublisherServiceImpl implements EventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;



    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

@Override
public void publishRoleAssignment(String userId, String role) {
    RoleAssignmentEvent event = new RoleAssignmentEvent(userId, role);
    try {
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(ROLE_ASSIGNMENT_TOPIC, userId, event);
            
        future.get();
    } catch (Exception e) {
        log.error("Failed to publish role assignment event for user {}: {}", userId, e.getMessage());
        throw new EventPublishingException("Failed to publish role assignment event", e);
    }
}


    @Override
    public void publishUserCreated(UserCreatedEvent event) {
        try {
            kafkaTemplate.send(USER_CREATE_TOPIC, event.getUserId(), event)
                    .whenComplete((result,ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish UserCreatedEvent for userId={} to topic={}. Event: {}",
                                    event.getUserId(), USER_CREATE_TOPIC, event, ex);
                            //TODO simply retry mechanic

                        } else {
                            log.info("Published UserCreatedEvent for userId={} to topic={} at offset={}",
                                    event.getUserId(), USER_CREATE_TOPIC, result.getRecordMetadata().offset());
                        }
                    });
        }catch (Exception e) {
            log.error("Critical publish failure for user {}: {}", event.getUserId(), e.getMessage());
        }
    }

    @Override
    public void publishEmailRequest(EmailRequestEvent event) {
        try {
            kafkaTemplate.send(EMAIL_SEND_TOPIC, event)
                    .thenAccept(result -> {
                        log.info("Mail send result: {}" , result);
                    })
                    .exceptionally(ex -> {
                        log.error("Published failed for event: {}", event ,ex);
                        throw  new EventPublishingException("Critical publishing failure");
                    });
        }catch (Exception e){
            log.error("Critical publish failure for email {}: {}", event.getEmail(), e.getMessage());
        }
    }
}