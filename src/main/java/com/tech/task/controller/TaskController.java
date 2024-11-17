package com.tech.task.controller;

import com.tech.task.dto.request.CommentRequest;
import com.tech.task.dto.request.CreateOrUpdateTaskRequest;
import com.tech.task.dto.response.TaskResponse;
import com.tech.task.dto.request.UpdateTaskStatusRequest;
import com.tech.task.service.impl.TaskServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<Object> updateTask(@RequestBody CreateOrUpdateTaskRequest createTaskRequest, @PathVariable Long id) {
        taskService.updateTask(id, createTaskRequest);
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

    @PostMapping("/{taskId}/comment")
    public ResponseEntity<Object> addCommentToTask(@PathVariable Long taskId, @RequestBody CommentRequest commentRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskService.addCommentToTask(taskId, commentRequest, username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{taskId}/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> changeTaskStatus(@PathVariable Long taskId, @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskService.updateTaskStatus(taskId, updateTaskStatusRequest, username);
        return ResponseEntity.ok().build();
    }
}
