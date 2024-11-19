package com.tech.task.controller;

import com.tech.task.dto.request.CommentRequest;
import com.tech.task.dto.request.CreateOrUpdateTaskRequest;
import com.tech.task.dto.request.UpdateTaskPriorityRequest;
import com.tech.task.dto.response.TaskResponse;
import com.tech.task.dto.request.UpdateTaskStatusRequest;
import com.tech.task.model.Task;
import com.tech.task.model.state.Priority;
import com.tech.task.model.state.Status;
import com.tech.task.repository.config.CustomPageable;
import com.tech.task.service.impl.TaskServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@Tag(name = "Task Management", description = "Endpoints for managing tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskServiceImpl taskService;

    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Get tasks by executor ID", description = "Retrieve tasks assigned to a specific executor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/executor/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TaskResponse>> getTaskByExecutorId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = new CustomPageable(page, size);
        Page<TaskResponse> tasks = taskService.getTaskByExecutorId(id, pageable);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get tasks by creator ID", description = "Retrieve tasks created by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/creator/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TaskResponse>> getTaskByCreator(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = new CustomPageable(page, size);
        Page<TaskResponse> tasks = taskService.getTaskByCreator(id, pageable);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get task by ID", description = "Retrieve a specific task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Get all tasks", description = "Retrieve all tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<TaskResponse> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = new CustomPageable(page, size);
        return taskService.getAllTasks(pageable);
    }

    @Operation(summary = "Create a new task", description = "Create a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createTask(@RequestBody CreateOrUpdateTaskRequest createTaskRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskService.createTask(createTaskRequest, username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add a comment to a task", description = "Add a comment to a specific task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/{taskId}/comment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> addCommentToTask(@PathVariable Long taskId, @RequestBody CommentRequest commentRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskService.addCommentToTask(taskId, commentRequest, username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Assign executors to a task", description = "Assign executors to a specific task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/{taskId}/executors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignExecutors(
            @PathVariable Long taskId,
            @RequestBody List<Long> executorIds) {
        taskService.assignExecutors(taskId, executorIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update a task", description = "Update a specific task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> updateTask(@RequestBody CreateOrUpdateTaskRequest createTaskRequest, @PathVariable Long id) {
        taskService.updateTask(id, createTaskRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update task priority", description = "Update the priority of a specific task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{taskId}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateTaskPriority(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskPriorityRequest updateTaskPriorityRequest) {
        taskService.updateTaskPriority(taskId, updateTaskPriorityRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change task status", description = "Change the status of a specific task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{taskId}/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> changeTaskStatus(@PathVariable Long taskId, @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskService.updateTaskStatus(taskId, updateTaskStatusRequest, username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete a task by ID", description = "Delete a specific task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete comments from a task", description = "Delete specific comments from a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{taskId}/comments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCommentById(
            @PathVariable Long taskId,
            @RequestBody List<Long> commentsId) {
        taskService.deleteCommentById(taskId, commentsId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete executors from a task", description = "Delete specific executors from a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{taskId}/executors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExecutorsByIds(
            @PathVariable Long taskId,
            @RequestBody List<Long> executorIds) {
        taskService.deleteExecutorsByIds(taskId, executorIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tasks")
    public Page<Task> getTasksByFilter(@RequestParam(required = false) Status status,
                                       @RequestParam(required = false) Priority priority,
                                       @RequestParam(required = false) Long creatorId,
                                       @RequestParam(required = false) Long executorId,
                                       Pageable pageable) {
        return taskService.getTasksByFilter(status, priority, creatorId, executorId, pageable);
    }
}
