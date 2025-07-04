package com.home.preorder.service.service.impl;

import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.service.TaskQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskQueryServiceImpl implements TaskQueryService {
    @Override
    public TaskDTO getTaskById(String taskId) {
        return null;
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
