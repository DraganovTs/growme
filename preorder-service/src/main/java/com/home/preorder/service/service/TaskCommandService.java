package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.dto.TaskStatusUpdateRequestDTO;

import java.util.UUID;

public interface TaskCommandService {

    TaskDTO createTask(TaskDTO taskDTO);

    TaskDTO updateTaskStatus(String taskId, TaskStatusUpdateRequestDTO status);

    void cancelTask(UUID taskId , UUID userId);

}
