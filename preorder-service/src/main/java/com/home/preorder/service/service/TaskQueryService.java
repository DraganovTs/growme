package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.TaskDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskQueryService {
    TaskDTO getTaskById(String taskId);
    Page<TaskDTO> getAllTasks(Pageable pageable);
    Page<TaskDTO> getTasksByUser(String userId, Pageable pageable);
    Page<TaskDTO> searchTasks(String query, String category, Pageable pageable);
}
