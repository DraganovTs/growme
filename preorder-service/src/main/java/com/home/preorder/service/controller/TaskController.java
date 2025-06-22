package com.home.preorder.service.controller;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/task",produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    @PostMapping
    public ResponseEntity<?> createTask(){
        return null;
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTask(@PathVariable String taskId){
        return null;
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable String taskId,
                                        @RequestBody String status){
        return null;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getTaskByUserId( @PathVariable String userId){
        return null;
    }

    //TODO



}
