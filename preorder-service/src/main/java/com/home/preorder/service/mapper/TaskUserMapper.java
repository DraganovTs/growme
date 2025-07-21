package com.home.preorder.service.mapper;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.preorder.service.model.entity.TaskUser;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TaskUserMapper {
    public TaskUser mapEventToTaskUser(UserCreatedEvent event) {

        return TaskUser.builder()
                .taskUserId(UUID.fromString(event.getUserId()))
                .taskUserName(event.getUserName())
                .taskUserEmail(event.getUserEmail())
                .build();
    }
}
