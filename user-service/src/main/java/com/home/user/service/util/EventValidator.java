package com.home.user.service.util;

import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.growme.common.module.events.ProductDeletionToUserEvent;
import com.home.growme.common.module.events.RoleAssignmentResult;
import org.springframework.stereotype.Component;

@Component
public class EventValidator {

    public void validateRoleAssignmentResult(RoleAssignmentResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Role assignment result cannot be null");
        }
        if (result.getUserId() == null || result.getUserId().isEmpty()) {
            throw new IllegalArgumentException("User ID is required in role assignment");
        }
    }

    public void validateProductAssignment(ProductAssignedToUserEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Product assignment event cannot be null");
        }
        if (event.getUserId() == null || event.getUserId().isEmpty()) {
            throw new IllegalArgumentException("User ID is required in product assignment");
        }
        if (event.getProductId() == null || event.getProductId().isEmpty()) {
            throw new IllegalArgumentException("Product ID is required");
        }
    }

    public void validateProductDeletion(ProductDeletionToUserEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Product assignment event cannot be null");
        }
        if (event.getUserId() == null || event.getUserId().isEmpty()) {
            throw new IllegalArgumentException("User ID is required in product assignment");
        }
        if (event.getProductId() == null || event.getProductId().isEmpty()) {
            throw new IllegalArgumentException("Product ID is required");
        }
    }
}
