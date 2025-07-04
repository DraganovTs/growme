package com.home.preorder.service.service.impl;

import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.enums.TaskStatus;
import com.home.preorder.service.service.PreorderService;
import com.home.preorder.service.service.TaskCommandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PreorderServiceImpl implements PreorderService {

    private final TaskCommandService taskCommandService;

    public PreorderServiceImpl(TaskCommandService taskCommandService) {
        this.taskCommandService = taskCommandService;
    }


    @Override
    public TaskDTO requestTaskCreation(TaskDTO taskDTO) {
        return taskCommandService.createTask(taskDTO);
    }

    @Override
    public TaskDTO requestUpdateTaskStatus(UUID taskId, TaskStatus taskStatus) {
        return null;
    }

    @Override
    public TaskDTO requestGetTaskById(UUID taskId) {
        return null;
    }

    @Override
    public Page<TaskDTO> requestGetTaskByUser(UUID userId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<TaskDTO> requestAllTasks(Pageable pageable) {
        return null;
    }
}
