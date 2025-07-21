package com.home.preorder.service.service.impl;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.preorder.service.exception.TaskUserAlreadyExistException;
import com.home.preorder.service.service.EventHandlerService;
import com.home.preorder.service.service.TaskUserService;
import com.home.preorder.service.util.EventValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.USER_CREATE_TOPIC;

@Slf4j
@Service
public class EventHandlerServiceImpl implements EventHandlerService {

    private final TaskUserService taskUserService;
    private final EventValidator eventValidator;

    public EventHandlerServiceImpl(TaskUserService taskUserService, EventValidator eventValidator) {
        this.taskUserService = taskUserService;
        this.eventValidator = eventValidator;
    }


    @Override
    @KafkaListener(topics = USER_CREATE_TOPIC)
    @Transactional
    public void handleUserCreatedEvent(UserCreatedEvent event) {

        try {
            eventValidator.validateUserCreatedEvent(event);
            log.info("Processing user creation event for user: {}", event.getUserId());

            taskUserService.createUser(event);
            log.info("Successfully created owner for user: {}", event.getUserId());

        } catch (TaskUserAlreadyExistException e){
            log.warn("Task user already exists for user: {}", event.getUserId());
        }catch (Exception e){
            log.error("Failed to process user creation event for user: {}", event.getUserId(), e);
            throw e;
        }
    }
}
