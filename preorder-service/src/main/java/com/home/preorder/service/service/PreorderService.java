package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.dto.TaskStatusUpdateRequestDTO;
import com.home.preorder.service.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PreorderService {


    TaskDTO requestTaskCreation(TaskDTO taskDTO);

    TaskDTO  requestUpdateTaskStatus(String taskId, TaskStatusUpdateRequestDTO taskStatus);

    TaskDTO requestTaskById(String taskId);

    Page<TaskDTO> requestTaskByUser(UUID userId, Pageable pageable);

    Page<TaskDTO> requestAllTasks(Pageable pageable);
}
