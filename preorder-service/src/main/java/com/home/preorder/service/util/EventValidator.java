package com.home.preorder.service.util;

import com.home.growme.common.module.events.CategoryCreationEvent;
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

    public void validateCategoryCreatedEvent(CategoryCreationEvent event) {
        if (event == null){
            throw new IllegalArgumentException("Category created event cannot be null");
        }
        if (event.getCategoryId() == null || event.getCategoryId().isEmpty()){
            throw new IllegalArgumentException("Category ID is required in category assignment");
        }
        if (event.getCategoryName() == null || event.getCategoryName().isEmpty()){
            throw new IllegalArgumentException("Category name is required in category assignment");
        }
    }
}
