package com.home.preorder.service.service.impl;

import com.home.preorder.service.model.entity.Task;
import com.home.preorder.service.model.enums.TaskStatus;
import com.home.preorder.service.repository.TaskRepository;
import com.home.preorder.service.service.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private static final int TASK_EXPIRATION_MONTHS = 1;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    @Override
    public void cleanupExpiredTasks() {
        LocalDateTime threshold = LocalDateTime.now().minusMonths(TASK_EXPIRATION_MONTHS);

        List<Task> expiredTasks = taskRepository.findAllByCreatedAtBeforeAndStatusNot(
                threshold,
                TaskStatus.ARCHIVED
        );


        taskRepository.deleteAll(expiredTasks);


        if (!expiredTasks.isEmpty()) {
            System.out.println("Deleted " + expiredTasks.size() + " expired tasks");
        }
    }
}
