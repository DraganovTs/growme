package com.home.user.service.service;

import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.growme.common.module.events.RoleAssignmentResult;

public interface EventHandlerService {
    void handleRoleAssignmentResult(RoleAssignmentResult result);
    void handleProductAssignment(ProductAssignedToUserEvent event);
}
