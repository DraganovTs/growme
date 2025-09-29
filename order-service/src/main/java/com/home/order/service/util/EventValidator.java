package com.home.order.service.util;

import com.home.growme.common.module.events.UserCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class EventValidator {

    public void validateUserCreatedEvent(UserCreatedEvent event){
        if (event == null){
            throw new IllegalArgumentException("User created event cannot be null");
        }
        if (event.getUserId() == null || event.getUserId().isEmpty()){
            throw new IllegalArgumentException("User ID is required in user assignment");
        }
        if (event.getUserName() == null || event.getUserName().isEmpty()){
            throw new IllegalArgumentException("User name is required in user assignment");
        }
        if (event.getUserEmail() == null || event.getUserEmail().isEmpty()){
            throw new IllegalArgumentException("User email is required in user assignment");
        }
    }


}
