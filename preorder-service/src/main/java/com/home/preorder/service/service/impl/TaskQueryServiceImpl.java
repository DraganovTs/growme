package com.home.preorder.service.service.impl;

import com.home.preorder.service.exception.TaskNotFoundException;
import com.home.preorder.service.mapper.TaskMapper;
import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.entity.Task;
import com.home.preorder.service.repository.TaskRepository;
import com.home.preorder.service.service.TaskQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TaskQueryServiceImpl implements TaskQueryService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskQueryServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskDTO getTaskById(String taskId) {
        Task task = taskRepository.findById(UUID.fromString(taskId))
                .orElseThrow(() -> new TaskNotFoundException("Task not found whit Id: " + taskId));
        return taskMapper.mapTaskToTaskDTO(task);
    }

    @Override
    public Page<TaskDTO> getAllTasks(Pageable pageable) {
        return null;
    }

    @Override
    public Page<TaskDTO> getTasksByUser(String userId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<TaskDTO> searchTasks(String query, String category, Pageable pageable) {
        return null;
    }
}
