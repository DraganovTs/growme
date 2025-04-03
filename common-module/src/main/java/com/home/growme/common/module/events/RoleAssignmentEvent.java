package com.home.growme.common.module.events;

import lombok.Data;

@Data
public class RoleAssignmentEvent extends Event{
    private final String userId;
    private final String roleName;

    public RoleAssignmentEvent(String userId, String roleName) {
        this.userId = userId;
        this.roleName = roleName;
    }
}
