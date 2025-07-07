package com.home.preorder.service.service.impl;

import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.dto.TaskStatusUpdateRequestDTO;
import com.home.preorder.service.service.PreorderService;
import com.home.preorder.service.service.TaskCommandService;
import com.home.preorder.service.service.TaskQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PreorderServiceImpl implements PreorderService {

    private final TaskCommandService taskCommandService;
    private final TaskQueryService taskQueryService;

    public PreorderServiceImpl(TaskCommandService taskCommandService, TaskQueryService taskQueryService) {
        this.taskCommandService = taskCommandService;
        this.taskQueryService = taskQueryService;
    }


    @Override
    public TaskDTO requestTaskCreation(TaskDTO taskDTO) {
        return taskCommandService.createTask(taskDTO);
    }

    @Override
    public TaskDTO requestUpdateTaskStatus(String taskId, TaskStatusUpdateRequestDTO taskStatus) {
        return taskCommandService.updateTaskStatus(taskId, taskStatus);
    }

    @Override
    public TaskDTO requestTaskById(String taskId) {
        return taskQueryService.getTaskById(taskId);
    }

    @Override
    public Page<TaskDTO> requestTaskByUser(UUID userId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<TaskDTO> requestAllTasks(Pageable pageable) {
        return null;
    }

    //todo
}
