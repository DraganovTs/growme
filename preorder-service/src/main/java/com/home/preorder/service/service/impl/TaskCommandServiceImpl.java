package com.home.preorder.service.service.impl;

import com.home.preorder.service.mapper.TaskMapper;
import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.entity.Category;
import com.home.preorder.service.model.entity.Task;
import com.home.preorder.service.model.enums.TaskStatus;
import com.home.preorder.service.repository.TaskRepository;
import com.home.preorder.service.service.CategoryService;
import com.home.preorder.service.service.TaskCommandService;
import org.springframework.stereotype.Service;

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
        return taskMapper.mapTaskToTaskDTO(taskRepository.save(task));
    }

    @Override
    public TaskDTO updateTaskStatus(String taskId, String status) {
        return null;
    }

    @Override
    public void cancelTask(String taskId) {

    }
}
