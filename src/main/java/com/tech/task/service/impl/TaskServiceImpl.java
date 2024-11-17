package com.tech.task.service.impl;

import com.tech.task.dto.request.CreateOrUpdateTaskRequest;
import com.tech.task.dto.request.CommentRequest;
import com.tech.task.dto.request.UpdateTaskStatusRequest;
import com.tech.task.dto.response.TaskResponse;
import com.tech.task.model.Comment;
import com.tech.task.model.Task;
import com.tech.task.model.User;
import com.tech.task.model.role.RoleEnum;
import com.tech.task.model.state.Priority;
import com.tech.task.model.state.Status;
import com.tech.task.repository.TaskRepository;
import com.tech.task.service.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserServiceImpl userService;
    private final ModelMapper modelMapper;

    public TaskServiceImpl(TaskRepository taskRepository, UserServiceImpl userService, ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    public List<TaskResponse> getAllTask() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .toList();
    }

    public void createTask(CreateOrUpdateTaskRequest createTaskRequest, String username) {
        User creator = userService.getByUsername(username);
        List<User> executors = createTaskRequest.getExecutorsIds() != null ?
                createTaskRequest.getExecutorsIds().stream()
                        .map(userService::getById)
                        .collect(Collectors.toList()) : List.of();

        Task task = modelMapper.map(createTaskRequest, Task.class);
        task.setCreator(creator);
        task.setExecutors(executors);

        task = taskRepository.save(task);
        modelMapper.map(task, CreateOrUpdateTaskRequest.class);
    }

    public void updateTask(Long taskId, CreateOrUpdateTaskRequest updateTaskRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        task.setTitle(updateTaskRequest.getTitle());
        task.setDescription(updateTaskRequest.getDescription());
        task.setStatus(updateTaskRequest.getStatus());
        task.setPriority(updateTaskRequest.getPriority());

        List<User> executors = updateTaskRequest.getExecutorsIds() != null ?
                updateTaskRequest.getExecutorsIds().stream()
                        .map(userService::getById)
                        .collect(Collectors.toList()) : List.of();
        task.setExecutors(executors);

        task = taskRepository.save(task);

        modelMapper.map(task, TaskResponse.class);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public List<TaskResponse> getTaskByExecutorId(Long id) {
        List<Task> tasks = taskRepository.getTaskByExecutorsId(id);
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .toList();
    }

    public TaskResponse getTaskByCreator(Long id) {
        Task task = taskRepository.getTaskByCreatorId(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return modelMapper.map(task, TaskResponse.class);
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found!"));
        return modelMapper.map(task, TaskResponse.class);
    }

    public void addCommentToTask(Long taskId, CommentRequest commentRequest, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        User user = userService.getByUsername(username);
        if(isUserAllowedToChangeTask(user, task)) {
            Comment comment = new Comment();
            comment.setText(commentRequest.getText());
            comment.setUser(user);
            comment.setTask(task);

            task.getComments().add(comment);
            taskRepository.save(task);
        } else {
            throw new RuntimeException("You are not allowed to add comments to task!");
        }
    }

    public void deleteCommentById(Long taskId, List<Long> commentsId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        List<Comment> commentsToRemove = task.getComments().stream()
                .filter(comment -> commentsId.contains(comment.getId()))
                .toList();

        task.getComments().removeAll(commentsToRemove);
        taskRepository.save(task);
    }

    public void deleteExecutorsByIds(Long taskId, List<Long> executorIds) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        List<User> executorsToRemove = executorIds.stream()
                .map(userService::getById)
                .toList();

        task.getExecutors().removeAll(executorsToRemove);
        taskRepository.save(task);
    }

    public void assignExecutors(Long taskId, List<Long> executorIds) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        List<User> executorsToAssign = executorIds.stream()
                .map(userService::getById)
                .toList();

        task.getExecutors().addAll(executorsToAssign);
        taskRepository.save(task);
    }

    public void updateTaskStatus(Long taskId, UpdateTaskStatusRequest updateTaskStatusRequest, String username) {
        User user = userService.getByUsername(username);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        if (isUserAllowedToChangeTask(user, task)) {
            task.setStatus(updateTaskStatusRequest.getStatus());
            taskRepository.save(task);
        } else {
            throw new RuntimeException("You are not allowed to change task status");
        }
    }


    public void updateTaskPriority(Long taskId, Priority priority) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        task.setPriority(priority);
        taskRepository.save(task);
    }

    private boolean isUserAllowedToChangeTask(User user, Task task) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(RoleEnum.ROLE_ADMIN)) ||
                task.getExecutors().contains(user);
    }
}
