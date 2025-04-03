package com.home.growme.common.module.events;

import lombok.Data;

@Data
public class RoleAssignmentResult extends Event{
    private final String correlationId;
    private final String userId;
    private final boolean success;
    //TODO add error details


    public RoleAssignmentResult(String correlationId, String userId, boolean success) {
        this.correlationId = correlationId;
        this.userId = userId;
        this.success = success;
    }
}
