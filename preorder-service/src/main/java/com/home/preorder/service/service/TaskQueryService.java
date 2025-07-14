package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.dto.TaskResponseListDTO;
import com.home.preorder.service.specification.TaskSpecParams;


import java.util.List;

public interface TaskQueryService {
    TaskDTO getTaskById(String taskId);
    TaskResponseListDTO getAllTasks(TaskSpecParams request);
    List<TaskDTO> getTasksByUser(TaskSpecParams request);

}
