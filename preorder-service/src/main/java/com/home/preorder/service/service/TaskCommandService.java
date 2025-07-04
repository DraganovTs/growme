package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.TaskDTO;

public interface TaskCommandService {

    TaskDTO createTask(TaskDTO taskDTO);

    TaskDTO updateTaskStatus(String taskId, String status);

    void cancelTask(String taskId);

}
