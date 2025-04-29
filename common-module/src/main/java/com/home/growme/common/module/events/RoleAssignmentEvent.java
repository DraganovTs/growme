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

    @JsonCreator
    public RoleAssignmentEvent(
            @JsonProperty("userId") String userId,
            @JsonProperty("roleName") String roleName) {
        this.userId = userId;
        this.roleName = roleName;
    }
}
