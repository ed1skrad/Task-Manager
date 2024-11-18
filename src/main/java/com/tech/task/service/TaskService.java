package com.tech.task.service;

import com.tech.task.dto.request.CommentRequest;
import com.tech.task.dto.request.CreateOrUpdateTaskRequest;
import com.tech.task.dto.request.UpdateTaskPriorityRequest;
import com.tech.task.dto.request.UpdateTaskStatusRequest;
import com.tech.task.dto.response.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TaskService {

    Page<TaskResponse> getAllTasks(Pageable pageable);

    void createTask(CreateOrUpdateTaskRequest createTaskRequest, String username);

    void updateTask(Long taskId, CreateOrUpdateTaskRequest updateTaskRequest);

    void deleteTask(Long taskId);

    Page<TaskResponse> getTaskByExecutorId(Long id, Pageable pageable);

    Page<TaskResponse> getTaskByCreator(Long id, Pageable pageable);

    TaskResponse getTaskById(Long id);

    void addCommentToTask(Long taskId, CommentRequest commentRequest, String username);

    void deleteCommentById(Long taskId, List<Long> commentsId);

    void deleteExecutorsByIds(Long taskId, List<Long> executorIds);

    void assignExecutors(Long taskId, List<Long> executorIds);

    void updateTaskPriority(Long taskId, UpdateTaskPriorityRequest updateTaskPriorityRequest);

    void updateTaskStatus(Long taskId, UpdateTaskStatusRequest updateTaskStatusRequest, String username);
}
