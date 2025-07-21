package com.home.preorder.service.service.impl;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.preorder.service.exception.TaskUserAlreadyExistException;
import com.home.preorder.service.exception.TaskUserNotFoundException;
import com.home.preorder.service.mapper.TaskUserMapper;
import com.home.preorder.service.model.entity.TaskUser;
import com.home.preorder.service.repository.TaskUserRepository;
import com.home.preorder.service.service.TaskUserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TaskUserServiceImpl implements TaskUserService {

    private final TaskUserRepository taskUserRepository;
    private final TaskUserMapper taskUserMapper;

    public TaskUserServiceImpl(TaskUserRepository taskUserRepository, TaskUserMapper taskUserMapper) {
        this.taskUserRepository = taskUserRepository;
        this.taskUserMapper = taskUserMapper;
    }


    @Override
    public TaskUser findUserById(UUID id) {
        return taskUserRepository.findById(id).orElseThrow(() -> new TaskUserNotFoundException("User not found whit Id:" + id));
    }

    @Override
    public void createUser(UserCreatedEvent event) {
        if (taskUserRepository.existsById(UUID.fromString(event.getUserId()))) {
            throw new TaskUserAlreadyExistException(event.getUserId());
        }

        TaskUser taskUser = taskUserMapper.mapEventToTaskUser(event);
        taskUserRepository.save(taskUser);
    }

    @Override
    public String findUserEmailByUserId(UUID taskUserId) {
        TaskUser taskUser = findUserById(taskUserId);
        return taskUser.getTaskUserEmail();
    }
}
