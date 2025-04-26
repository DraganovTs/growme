package com.home.growme.produt.service.kafka.publisher;

import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductEventPublisher {

    private static final String PRODUCT_ASSIGNMENT_TOPIC = "product.user.assignment";
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public ProductEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

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


    public void publishProductDeletion() {

    }
}
