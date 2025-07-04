package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PreorderService {


    TaskDTO requestTaskCreation(TaskDTO taskDTO);

    TaskDTO  requestUpdateTaskStatus(UUID taskId, TaskStatus taskStatus);

    TaskDTO requestGetTaskById(UUID taskId);

    Page<TaskDTO> requestGetTaskByUser(UUID userId, Pageable pageable);

    Page<TaskDTO> requestAllTasks(Pageable pageable);
}
