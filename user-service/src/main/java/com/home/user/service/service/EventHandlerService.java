package com.home.user.service.service;

import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.growme.common.module.events.RoleAssignmentResult;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface EventHandlerService {
    void handleRoleAssignmentResult(RoleAssignmentResult result);

    void handleProductAssignment(ProductAssignedToUserEvent event,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic);
}
