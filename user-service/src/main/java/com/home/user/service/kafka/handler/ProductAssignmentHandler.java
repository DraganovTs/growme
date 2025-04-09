package com.home.user.service.kafka.handler;

import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.user.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductAssignmentHandler {

    private static final String PRODUCT_ASSIGNMENT_TOPIC = "product.user.assignment";
    private final UserService userService;

    public ProductAssignmentHandler(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics = PRODUCT_ASSIGNMENT_TOPIC)
    public void handleProductAssignment(ProductAssignedToUserEvent event){
     log.info("Received product assignment event: {}", event);
     userService.addProductToUser(event.getUserId(),event.getProductId());
     log.info("Finished product assignment event: {}", event);
    }
}
