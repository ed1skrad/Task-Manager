package com.tech.task.controller;

import com.tech.task.dto.request.CreateTaskRequest;
import com.tech.task.service.impl.TaskServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    private final TaskServiceImpl taskService;

    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @PostMapping()
    public ResponseEntity<Object> createTask(@RequestBody CreateTaskRequest createTaskRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskService.createTask(createTaskRequest, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}