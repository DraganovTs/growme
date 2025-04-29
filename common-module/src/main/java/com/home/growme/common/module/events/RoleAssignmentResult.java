package com.home.growme.common.module.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RoleAssignmentResult extends Event{
    private final String correlationId;
    private final String userId;
    private final boolean success;
    //TODO add error details

    @JsonCreator
    public RoleAssignmentResult(@JsonProperty("correlationId") String correlationId,
                                @JsonProperty("userId") String userId,
                                @JsonProperty("success") boolean success){
        this.correlationId = correlationId;
        this.userId = userId;
        this.success = success;
    }
}
