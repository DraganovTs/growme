package com.home.growme.common.module.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public final class UserCreatedEvent extends Event {

    private final String userId;
    private final String userName;
    private final String operationType;

    @JsonCreator
    public UserCreatedEvent(
            @JsonProperty("userId") String userId,
            @JsonProperty("userName") String userName,
            @JsonProperty("operationType") String operationType) {
        this.userId = userId;
        this.userName = userName;
        this.operationType = operationType;
    }
}
