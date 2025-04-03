package com.home.growme.common.module.events;

import lombok.Data;

@Data
public class UserCreatedEvent extends Event{
    private final String userId;
    private final String userName;

    public UserCreatedEvent(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
