package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.dto.TaskResponseListDTO;
import com.home.preorder.service.model.dto.TaskStatusUpdateRequestDTO;
import com.home.preorder.service.specification.TaskSpecParams;

import java.util.List;
import java.util.UUID;

public interface PreorderService {


    TaskDTO requestTaskCreation(TaskDTO taskDTO);

    TaskDTO  requestUpdateTaskStatus(String taskId, TaskStatusUpdateRequestDTO taskStatus);

    TaskDTO requestTaskById(String taskId);

    List<TaskDTO> requestTaskByUser(TaskSpecParams request);

    TaskResponseListDTO requestAllTasks(TaskSpecParams request);
}
