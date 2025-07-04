package com.home.preorder.service.controller;


import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.service.PreorderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTask(@PathVariable String taskId) {
        return null;
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable String taskId,
                                        @RequestBody String status) {
        return null;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getTaskByUserId(@PathVariable String userId) {
        return null;
    }

    //TODO


}
