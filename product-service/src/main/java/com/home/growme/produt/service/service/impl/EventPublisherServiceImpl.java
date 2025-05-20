package com.home.growme.produt.service.service.impl;

import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.growme.common.module.events.ProductDeletionToUserEvent;
import com.home.growme.produt.service.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PRODUCT_ASSIGNMENT_TOPIC;
import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PRODUCT_DELETION_TOPIC;

@Slf4j
@Service
public class EventPublisherServiceImpl implements EventPublisherService {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


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
}
