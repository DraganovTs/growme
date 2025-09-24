package com.home.preorder.service.service;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.preorder.service.model.entity.TaskUser;

import java.util.UUID;

public interface TaskUserService {

    TaskUser findUserById(UUID id);

    void createUser(UserCreatedEvent event);

    String findUserEmailByUserId(UUID taskUserId);

    boolean existByUserId(String userId);
}
