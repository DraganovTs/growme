package com.home.user.service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductAssignmentHandler {

    public static final String PRODUCT_ASSIGNMENT_TOPIC = "product.user.assignment";
}
