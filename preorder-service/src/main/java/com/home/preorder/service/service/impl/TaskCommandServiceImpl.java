package com.home.preorder.service.service.impl;

import com.home.preorder.service.exception.TaskNotFoundException;
import com.home.preorder.service.exception.TaskStatusNotFoundException;
import com.home.preorder.service.exception.UnauthorizedActionException;
import com.home.preorder.service.mapper.TaskMapper;
import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.dto.TaskStatusUpdateRequestDTO;
import com.home.preorder.service.model.entity.Category;
import com.home.preorder.service.model.entity.Task;
import com.home.preorder.service.model.enums.TaskStatus;
import com.home.preorder.service.repository.TaskRepository;
import com.home.preorder.service.service.CategoryService;
import com.home.preorder.service.service.TaskCommandService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TaskCommandServiceImpl implements TaskCommandService {


    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final CategoryService categoryService;

    public TaskCommandServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, CategoryService categoryService) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.categoryService = categoryService;
    }

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        Category category = categoryService.getCategoryByName(taskDTO.getCategoryName());
        Task task = taskMapper.mapTaskDTOToTask(taskDTO);
        task.setCategory(category);
        task.setStatus(TaskStatus.OPEN);
        taskRepository.save(task);
        TaskDTO answer = taskMapper.mapTaskToTaskDTO(task);
        return answer;
    }

    @Override
    public TaskDTO updateTaskStatus(String taskId, TaskStatusUpdateRequestDTO status) {
        TaskStatus taskStatus;
        try {
            taskStatus = TaskStatus.valueOf(status.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TaskStatusNotFoundException("Task Status not found whit name: " + status.getStatus().toUpperCase());
        }
        Task task = taskRepository.findById(UUID.fromString(taskId))
                .orElseThrow(() -> new TaskNotFoundException("Task not found whit Id : " + taskId));
        task.setStatus(taskStatus);
        return taskMapper.mapTaskToTaskDTO(taskRepository.save(task));
    }

    @Override
    public void cancelTask(UUID taskId , UUID userId) {

        Task task = taskRepository.findById(taskId)
                        .orElseThrow(() -> new TaskNotFoundException("Task not found whit Id : " + taskId));

        if (!task.getUserId().equals(userId)){
            throw new UnauthorizedActionException("User is not authorized to create counter offer for this bid");
        }

        taskRepository.deleteById(taskId);
    }
}
