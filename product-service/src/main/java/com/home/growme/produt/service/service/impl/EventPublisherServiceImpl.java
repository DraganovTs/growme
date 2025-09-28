package com.home.growme.produt.service.service.impl;

import com.home.growme.common.module.events.CategoryCreationEvent;
import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.growme.common.module.events.ProductDeletionToUserEvent;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.growme.produt.service.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.*;

@Slf4j
@Service
public class EventPublisherServiceImpl implements EventPublisherService {

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY_MS = 500;
    private static final int RETRY_MULTIPLIER = 2;

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Retryable(
            retryFor = {EventPublishingException.class, ExecutionException.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY_MS, multiplier = RETRY_MULTIPLIER)
    )
    @Override
    public void publishProductAssignment(String userId, String productId) {
        ProductAssignedToUserEvent event = new ProductAssignedToUserEvent(userId, productId);

        try {
            kafkaTemplate.send(PRODUCT_ASSIGNMENT_TOPIC, event)
                    .thenAccept(result-> {
                        log.debug("Published product assignment whit id: {} for user {}",productId, userId);
                        // TODO: Add success metrics
                    })
                    .exceptionally(ex -> {
                        log.error("Publish failed for event: {}", event, ex);
                        // TODO: Add error metrics
                        return null;
                    });
        }catch (Exception e){
            log.error("Publish failed for event: {}", event, e);
            // TODO: Add dead letter queue handling
        }
    }
    @Retryable(
            retryFor = {EventPublishingException.class, ExecutionException.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY_MS, multiplier = RETRY_MULTIPLIER)
    )
    @Override
    public void publishProductDeletion(String productId, String ownerId) {
        ProductDeletionToUserEvent event = new ProductDeletionToUserEvent(ownerId,productId);

        try {
            kafkaTemplate.send(PRODUCT_DELETION_TOPIC,event)
                    .thenAccept(result -> {
                        log.debug("Published product deletion whit productId: {} for user {}",productId, ownerId);
                        // TODO: Add success metrics
                    })
                    .exceptionally(ex-> {
                        log.error("Publish failed for event: {}", event, ex);
                        // TODO: Add error metrics
                        return null;
                    });
        }catch (Exception e){
            log.error("Publish failed for event: {}", event, e);
            // TODO: Add dead letter queue handling
        }
    }
    @Retryable(
            retryFor = {EventPublishingException.class, ExecutionException.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY_MS, multiplier = RETRY_MULTIPLIER)
    )
    @Override
    public void publishCategoryCreation(String categoryId, String categoryName) {
        CategoryCreationEvent event =  new CategoryCreationEvent(categoryId,categoryName);

        try {
            kafkaTemplate.send(CATEGORY_CREATION_TOPIC,event)
                    .thenAccept(result -> {
                        log.debug("Published category creation whit categoryId: {} and categoryName {}",categoryId, categoryName);
                        // TODO: Add success metrics
                    })
                    .exceptionally(ex->{
                        log.error("Publish failed for event: {}", event, ex);
                        // TODO: Add error metrics
                        return null;
                    });
        }catch (Exception e){
            log.error("Publish failed for event: {}", event, e);
            // TODO: Add dead letter queue handling
        }
    }
}
