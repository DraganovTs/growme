package com.home.growme.common.module.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public final class UserCreatedEvent extends Event {

    private final String userId;
    private final String userName;

    @JsonCreator
    public UserCreatedEvent(
            @JsonProperty("userId") String userId,
            @JsonProperty("userName") String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
