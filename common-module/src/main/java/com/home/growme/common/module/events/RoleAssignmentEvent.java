package com.home.growme.common.module.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RoleAssignmentEvent extends Event {

    private final String userId;
    private final String roleName;
    private final String operationType;

    @JsonCreator
    public RoleAssignmentEvent(
            @JsonProperty("userId") String userId,
            @JsonProperty("roleName") String roleName,
            @JsonProperty("operationType") String operationType) {
        this.userId = userId;
        this.roleName = roleName;
        this.operationType = operationType;
    }
}
