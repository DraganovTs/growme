package com.home.user.service.kafka.handler;

import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.user.service.service.UserService;
import com.home.user.service.service.UserUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductAssignmentHandler {

    private static final String PRODUCT_ASSIGNMENT_TOPIC = "product.user.assignment";
    private final UserUpdateService userUpdateService;

    public ProductAssignmentHandler(UserUpdateService userUpdateService) {
        this.userUpdateService = userUpdateService;
    }


    @KafkaListener(topics = PRODUCT_ASSIGNMENT_TOPIC)
    public void handleProductAssignment(ProductAssignedToUserEvent event){
     log.info("Received product assignment event: {}", event);
     userUpdateService.addOwnedProduct(event.getUserId(),event.getProductId());
     log.info("Finished product assignment event: {}", event);
    }
}
