package com.home.growme.product.service.service.impl;

import com.home.growme.common.module.events.CategoryCreationEvent;
import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.growme.common.module.events.ProductDeletionToUserEvent;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.growme.product.service.config.EventMetrics;
import com.home.growme.product.service.model.enums.EventType;
import com.home.growme.product.service.service.EventPublisherService;
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

        try {
            kafkaTemplate.send(PRODUCT_ASSIGNMENT_TOPIC, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish ProductAssignment event: {}", event, ex);
                            eventMetrics.recordFailure(EventType.PRODUCT_ASSIGNMENT);
                        } else {
                            log.info("Published ProductAssignment event for user {} product {}", userId, productId);
                            eventMetrics.recordSuccess(EventType.PRODUCT_ASSIGNMENT);
                        }
                    });
        } catch (Exception e) {
            log.error("Publish failed for event: {}", event, e);
            eventMetrics.recordFailure(EventType.PRODUCT_ASSIGNMENT);
            // TODO: Add dead letter queue handling
            throw new EventPublishingException("Failed to publish product assignment", e);
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

        try {
            kafkaTemplate.send(PRODUCT_DELETION_TOPIC, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish ProductDeletion event: {}", event, ex);
                            eventMetrics.recordFailure(EventType.PRODUCT_DELETION);
                        } else {
                            log.info("Published ProductDeletion event for product {} owner {}", productId, ownerId);
                            eventMetrics.recordSuccess(EventType.PRODUCT_DELETION);
                        }
                    });
        } catch (Exception e) {
            log.error("Publish failed for event: {}", event, e);
            eventMetrics.recordFailure(EventType.PRODUCT_DELETION);
            // TODO: Add dead letter queue handling
            throw new EventPublishingException("Failed to publish product deletion", e);
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

        try {
            kafkaTemplate.send(CATEGORY_CREATION_TOPIC, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish CategoryCreation event: {}", event, ex);
                            eventMetrics.recordFailure(EventType.CATEGORY_CREATION);
                        } else {
                            log.info("Published CategoryCreation event category {} - {}", categoryId, categoryName);
                            eventMetrics.recordSuccess(EventType.CATEGORY_CREATION);
                        }
                    });
        } catch (Exception e) {
            log.error("Publish failed for event: {}", event, e);
            eventMetrics.recordFailure(EventType.CATEGORY_CREATION);
            // TODO: Add dead letter queue handling
            throw new EventPublishingException("Failed to publish category creation", e);
        }
    }
}
