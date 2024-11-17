package com.tech.task.controller;

import com.tech.task.dto.request.CommentRequest;
import com.tech.task.dto.request.CreateOrUpdateTaskRequest;
import com.tech.task.dto.request.UpdateTaskPriorityRequest;
import com.tech.task.dto.response.TaskResponse;
import com.tech.task.dto.request.UpdateTaskStatusRequest;
import com.tech.task.service.impl.TaskServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/executor/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TaskResponse>> getTaskByExecutorId(
            @PathVariable Long id,
            Pageable pageable) {
        Page<TaskResponse> tasks = taskService.getTaskByExecutorId(id, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/creator/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TaskResponse>> getTaskByCreator(
            @PathVariable Long id,
            Pageable pageable) {
        Page<TaskResponse> tasks = taskService.getTaskByCreator(id, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TaskResponse> getAllTasks(){
        return taskService.getAllTask();
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createTask(@RequestBody CreateOrUpdateTaskRequest createTaskRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskService.createTask(createTaskRequest, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{taskId}/comment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> addCommentToTask(@PathVariable Long taskId, @RequestBody CommentRequest commentRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskService.addCommentToTask(taskId, commentRequest, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{taskId}/executors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignExecutors(
            @PathVariable Long taskId,
            @RequestBody List<Long> executorIds) {
        taskService.assignExecutors(taskId, executorIds);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> updateTask(@RequestBody CreateOrUpdateTaskRequest createTaskRequest, @PathVariable Long id) {
        taskService.updateTask(id, createTaskRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{taskId}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateTaskPriority(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskPriorityRequest updateTaskPriorityRequest) {
        taskService.updateTaskPriority(taskId, updateTaskPriorityRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{taskId}/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> changeTaskStatus(@PathVariable Long taskId, @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskService.updateTaskStatus(taskId, updateTaskStatusRequest, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{taskId}/comments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCommentById(
            @PathVariable Long taskId,
            @RequestBody List<Long> commentsId) {
        taskService.deleteCommentById(taskId, commentsId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}/executors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExecutorsByIds(
            @PathVariable Long taskId,
            @RequestBody List<Long> executorIds) {
        taskService.deleteExecutorsByIds(taskId, executorIds);
        return ResponseEntity.ok().build();
    }
}
