package com.home.preorder.service.service.impl;

import com.home.preorder.service.exception.TaskNotFoundException;
import com.home.preorder.service.mapper.TaskMapper;
import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.dto.TaskResponseListDTO;
import com.home.preorder.service.model.entity.Task;
import com.home.preorder.service.repository.TaskRepository;
import com.home.preorder.service.service.TaskQueryService;
import com.home.preorder.service.specification.TaskSpecParams;
import com.home.preorder.service.specification.TaskSpecification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskQueryServiceImpl implements TaskQueryService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;

    public TaskQueryServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, TaskSpecification taskSpecification) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.taskSpecification = taskSpecification;
    }

    @Override
    public TaskDTO getTaskById(String taskId) {
        Task task = taskRepository.findById(UUID.fromString(taskId))
                .orElseThrow(() -> new TaskNotFoundException("Task not found whit Id: " + taskId));
        return taskMapper.mapTaskToTaskDTO(task);
    }

    @Override
    public TaskResponseListDTO getAllTasks(TaskSpecParams request) {
        Specification<Task> spec = taskSpecification.getTasks(request);
        Pageable pageable = PageRequest.of(
                request.getPageIndex() - 1,
                request.getPageSize() != null ? request.getPageSize() : 10
        );

        Page<Task> page = taskRepository.findAll(spec, pageable);

        return TaskResponseListDTO.builder()
                .totalPages(page.getTotalPages())
                .totalCount(page.getTotalElements())
                .pageIndex(request.getPageIndex())
                .pageSize(page.getSize())
                .dataList(page.getContent().stream()
                        .map(taskMapper::mapTaskToTaskDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<TaskDTO> getTasksByUser(TaskSpecParams request) {
        Specification<Task> spec = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("userId"), UUID.fromString(request.getUserId())));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        });

        Sort sort = getSorting(request.getSort());

        List<Task> tasks = taskRepository.findAll(spec, sort);

        return tasks.stream()
                .map(taskMapper::mapTaskToTaskDTO)
                .collect(Collectors.toList());
    }



    private Sort getSorting(String sortParam) {
        if (sortParam == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        switch (sortParam) {
            case "createdAtAsc":
                return Sort.by(Sort.Direction.ASC, "createdAt");
            case "createdAtDesc":
                return Sort.by(Sort.Direction.DESC, "createdAt");
            case "titleAsc":
                return Sort.by(Sort.Direction.ASC, "title");
            case "titleDesc":
                return Sort.by(Sort.Direction.DESC, "title");
            default:
                return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }
}
