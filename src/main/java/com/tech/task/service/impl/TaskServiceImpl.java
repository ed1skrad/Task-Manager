package com.tech.task.service.impl;

import com.tech.task.dto.request.CreateOrUpdateTaskRequest;
import com.tech.task.dto.request.CommentRequest;
import com.tech.task.dto.request.UpdateTaskPriorityRequest;
import com.tech.task.dto.request.UpdateTaskStatusRequest;
import com.tech.task.dto.response.TaskResponse;
import com.tech.task.exception.ForbiddenException;
import com.tech.task.exception.TaskNotFoundException;
import com.tech.task.model.Comment;
import com.tech.task.model.Task;
import com.tech.task.model.User;
import com.tech.task.model.role.RoleEnum;
import com.tech.task.model.state.Priority;
import com.tech.task.model.state.Status;
import com.tech.task.repository.TaskRepository;
import com.tech.task.repository.specification.TaskSpecification;
import com.tech.task.service.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserServiceImpl userService;
    private final ModelMapper modelMapper;
    private static final String TASK_NOT_FOUND_MESSAGE = "Task not found!";

    public TaskServiceImpl(TaskRepository taskRepository, UserServiceImpl userService, ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        Page<Task> tasks = taskRepository.findAll(pageable);
        return tasks.map(task -> modelMapper.map(task, TaskResponse.class));
    }

    public void createTask(CreateOrUpdateTaskRequest createTaskRequest, String username) {
        User creator = userService.getByUsername(username);
        List<User> executors = createTaskRequest.getExecutorsIds() != null ?
                createTaskRequest.getExecutorsIds().stream()
                        .map(userService::getById)
                        .toList() : List.of();

        Task task = modelMapper.map(createTaskRequest, Task.class);
        task.setCreator(creator);
        task.setExecutors(executors);

        task = taskRepository.save(task);
        modelMapper.map(task, CreateOrUpdateTaskRequest.class);
    }

    public void updateTask(Long taskId, CreateOrUpdateTaskRequest updateTaskRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

        task.setTitle(updateTaskRequest.getTitle());
        task.setDescription(updateTaskRequest.getDescription());
        task.setStatus(updateTaskRequest.getStatus());
        task.setPriority(updateTaskRequest.getPriority());

        List<User> executors = updateTaskRequest.getExecutorsIds() != null ?
                updateTaskRequest.getExecutorsIds().stream()
                        .map(userService::getById)
                        .toList() : List.of();
        task.setExecutors(executors);

        task = taskRepository.save(task);

        modelMapper.map(task, TaskResponse.class);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public Page<TaskResponse> getTaskByExecutorId(Long id, Pageable pageable) {
        Page<Task> tasks = taskRepository.getTaskByExecutorsId(id, pageable);
        return tasks.map(task -> modelMapper.map(task, TaskResponse.class));
    }

    public Page<TaskResponse> getTaskByCreator(Long id, Pageable pageable) {
        Page<Task> tasks = taskRepository.getTaskByCreatorId(id, pageable);
        return tasks.map(task -> modelMapper.map(task, TaskResponse.class));
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));
        return modelMapper.map(task, TaskResponse.class);
    }

    public void addCommentToTask(Long taskId, CommentRequest commentRequest, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

        User user = userService.getByUsername(username);
        if(isUserAllowedToChangeTask(user, task)) {
            Comment comment = new Comment();
            comment.setText(commentRequest.getText());
            comment.setUser(user);
            comment.setTask(task);

            task.getComments().add(comment);
            taskRepository.save(task);
        } else {
            throw new ForbiddenException("You are not allowed to add comments to task!");
        }
    }

    public void deleteCommentById(Long taskId, List<Long> commentsId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

        List<Comment> commentsToRemove = task.getComments().stream()
                .filter(comment -> commentsId.contains(comment.getId()))
                .toList();

        task.getComments().removeAll(commentsToRemove);
        taskRepository.save(task);
    }

    public void deleteExecutorsByIds(Long taskId, List<Long> executorIds) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

        List<User> executorsToRemove = executorIds.stream()
                .map(userService::getById)
                .toList();

        task.getExecutors().removeAll(executorsToRemove);
        taskRepository.save(task);
    }

    public void assignExecutors(Long taskId, List<Long> executorIds) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

        List<User> executorsToAssign = executorIds.stream()
                .map(userService::getById)
                .toList();

        task.getExecutors().addAll(executorsToAssign);
        taskRepository.save(task);
    }

    public void updateTaskPriority(Long taskId, UpdateTaskPriorityRequest updateTaskPriorityRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

        task.setPriority(updateTaskPriorityRequest.getPriority());
        taskRepository.save(task);
    }

    public void updateTaskStatus(Long taskId, UpdateTaskStatusRequest updateTaskStatusRequest, String username) {
        User user = userService.getByUsername(username);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

        if (isUserAllowedToChangeTask(user, task)) {
            task.setStatus(updateTaskStatusRequest.getStatus());
            taskRepository.save(task);
        } else {
            throw new ForbiddenException("You are not allowed to change task status");
        }
    }

    private boolean isUserAllowedToChangeTask(User user, Task task) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(RoleEnum.ROLE_ADMIN)) ||
                task.getExecutors().contains(user);
    }

    public Page<Task> getTasksByFilter(Status status, Priority priority, Long creatorId, Long executorId, Pageable pageable) {
        return taskRepository.findAll(TaskSpecification.filterTasks(status, priority, creatorId, executorId), pageable);
    }
}
