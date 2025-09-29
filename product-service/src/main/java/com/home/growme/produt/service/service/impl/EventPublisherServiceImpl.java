package com.home.growme.produt.service.service.impl;

import com.home.growme.common.module.events.CategoryCreationEvent;
import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.growme.common.module.events.ProductDeletionToUserEvent;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.growme.produt.service.config.EventMetrics;
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

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final EventMetrics eventMetrics;

    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate, EventMetrics eventMetrics) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventMetrics = eventMetrics;
    }

    @Retryable(
            retryFor = {EventPublishingException.class, ExecutionException.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY_MS, multiplier = RETRY_MULTIPLIER)
    )
    @Override
    public void publishProductAssignment(String userId, String productId) {
        ProductAssignedToUserEvent event = new ProductAssignedToUserEvent(userId, productId);
        var timerSample = eventMetrics.startProductAssignmentPublishTimer();

        try {
            kafkaTemplate.send(PRODUCT_ASSIGNMENT_TOPIC, event)
                    .thenAccept(result -> {
                        log.debug("Published product assignment whit id: {} for user {}", productId, userId);
                        eventMetrics.recordCategoryCreationPublishSuccess();
                    })
                    .exceptionally(ex -> {
                        log.error("Publish failed for event: {}", event, ex);
                        eventMetrics.recordProductAssignmentPublishFailure();
                        return null;
                    });
        } catch (Exception e) {
            log.error("Publish failed for event: {}", event, e);
            eventMetrics.recordProductAssignmentPublishFailure();
            // TODO: Add dead letter queue handling
            throw new EventPublishingException("Failed to publish product assignment", e);
        } finally {
            eventMetrics.stopProductAssignmentPublishTimer(timerSample);
        }
    }

    @Retryable(
            retryFor = {EventPublishingException.class, ExecutionException.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY_MS, multiplier = RETRY_MULTIPLIER)
    )
    @Override
    public void publishProductDeletion(String productId, String ownerId) {
        ProductDeletionToUserEvent event = new ProductDeletionToUserEvent(ownerId, productId);
        var timerSample = eventMetrics.startProductDeletionPublishTimer();

        try {
            kafkaTemplate.send(PRODUCT_DELETION_TOPIC, event)
                    .thenAccept(result -> {
                        log.debug("Published product deletion whit productId: {} for user {}", productId, ownerId);
                        eventMetrics.recordProductDeletionPublishSuccess();
                    })
                    .exceptionally(ex -> {
                        log.error("Publish failed for event: {}", event, ex);
                        eventMetrics.recordProductDeletionPublishFailure();
                        return null;
                    });
        } catch (Exception e) {
            log.error("Publish failed for event: {}", event, e);
            eventMetrics.recordProductDeletionPublishFailure();
            // TODO: Add dead letter queue handling
            throw new EventPublishingException("Failed to publish product deletion", e);
        } finally {
            eventMetrics.stopProductDeletionPublishTimer(timerSample);
        }
    }

    @Retryable(
            retryFor = {EventPublishingException.class, ExecutionException.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY_MS, multiplier = RETRY_MULTIPLIER)
    )
    @Override
    public void publishCategoryCreation(String categoryId, String categoryName) {
        CategoryCreationEvent event = new CategoryCreationEvent(categoryId, categoryName);
        var timerSample = eventMetrics.startCategoryCreationPublishTimer();

        try {
            kafkaTemplate.send(CATEGORY_CREATION_TOPIC, event)
                    .thenAccept(result -> {
                        log.debug("Published category creation whit categoryId: {} and categoryName {}", categoryId, categoryName);
                        eventMetrics.recordCategoryCreationPublishSuccess();
                    })
                    .exceptionally(ex -> {
                        log.error("Publish failed for event: {}", event, ex);
                        eventMetrics.recordCategoryCreationPublishFailure();
                        return null;
                    });
        } catch (Exception e) {
            log.error("Publish failed for event: {}", event, e);
            eventMetrics.recordCategoryCreationPublishFailure();
            // TODO: Add dead letter queue handling
            throw new EventPublishingException("Failed to publish category creation", e);
        }finally {
            eventMetrics.stopCategoryCreationPublishTimer(timerSample);
        }
    }
}
