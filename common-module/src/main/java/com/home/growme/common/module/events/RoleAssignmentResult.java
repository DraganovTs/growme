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
    private final String operationType;
    //TODO add error details

    @JsonCreator
    public RoleAssignmentResult(@JsonProperty("correlationId") String correlationId,
                                @JsonProperty("userId") String userId,
                                @JsonProperty("success") boolean success,
                                @JsonProperty("operationType") String operationType) {
        this.correlationId = correlationId;
        this.userId = userId;
        this.success = success;
        this.operationType = operationType;
    }
}
