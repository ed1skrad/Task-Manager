package com.tech.task.controller;

import com.tech.task.dto.request.CreateOrUpdateTaskRequest;
import com.tech.task.dto.response.TaskResponse;
import com.tech.task.service.impl.TaskServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    private final TaskServiceImpl taskService;

    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @PostMapping()
    public ResponseEntity<Object> createTask(@RequestBody CreateOrUpdateTaskRequest createTaskRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskService.createTask(createTaskRequest, username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreateOrUpdateTaskRequest> updateTask(@PathVariable Long id, @RequestBody CreateOrUpdateTaskRequest updateTaskRequest) {
        taskService.updateTask(id, updateTaskRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/creator/{id}")
    public TaskResponse getTaskByCreatorId(@PathVariable Long id){
        return taskService.getTaskByCreator(id);
    }

    @GetMapping("/executor/{id}")
    public List<TaskResponse> getTaskByExecutorId(@PathVariable Long id){
        return taskService.getTaskByExecutorId(id);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @GetMapping("/all")
    public List<TaskResponse> getAllTasks(){
        return taskService.getAllTask();
    }
}
