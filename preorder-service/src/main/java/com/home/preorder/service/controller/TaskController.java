package com.home.preorder.service.controller;


import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.dto.TaskResponseListDTO;
import com.home.preorder.service.model.dto.TaskStatusUpdateRequestDTO;
import com.home.preorder.service.service.PreorderService;
import com.home.preorder.service.specification.TaskSpecParams;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final PreorderService preorderService;

    public TaskController(PreorderService preorderService) {
        this.preorderService = preorderService;
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        System.out.println();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(preorderService.requestTaskCreation(taskDTO));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable String taskId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(preorderService.requestTaskById(taskId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable String taskId,
                                        @RequestBody TaskStatusUpdateRequestDTO status) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(preorderService.requestUpdateTaskStatus(taskId,status));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDTO>> getTaskByUserId(@PathVariable String userId) {
        TaskSpecParams params = new TaskSpecParams();
        params.setUserId(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(preorderService.requestTaskByUser(params));
    }

    @GetMapping()
    public ResponseEntity<TaskResponseListDTO> getAllTasks(TaskSpecParams request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(preorderService.requestAllTasks(request));
    }


}
