package com.home.preorder.service.repository;

import com.home.preorder.service.model.entity.Task;
import com.home.preorder.service.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> , JpaSpecificationExecutor<Task> {
    Page<Task> findAll(Pageable pageable);

    List<Task> findAllByCreatedAtBeforeAndStatusNot(LocalDateTime createdAtBefore, TaskStatus taskStatus);
}
