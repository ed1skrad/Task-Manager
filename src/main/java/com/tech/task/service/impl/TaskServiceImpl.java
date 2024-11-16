package com.tech.task.service.impl;

import com.tech.task.dto.request.CreateOrUpdateTaskRequest;
import com.tech.task.dto.request.CommentRequest;
import com.tech.task.dto.response.TaskResponse;
import com.tech.task.model.Comment;
import com.tech.task.model.Task;
import com.tech.task.model.User;
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

    public List<TaskResponse> getAllTask(){
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .toList();
    }

    public void createTask(CreateOrUpdateTaskRequest createTaskRequest, String username){
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

    public TaskResponse updateTask(Long taskId, CreateOrUpdateTaskRequest updateTaskRequest) {
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

        return modelMapper.map(task, TaskResponse.class);
    }

    public void deleteTask(Long taskId){
        taskRepository.deleteById(taskId);
    }

    public List<TaskResponse> getTaskByExecutorId(Long id){
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
                .orElseThrow(() ->  new RuntimeException("Task not found!"));
        return modelMapper.map(task, TaskResponse.class);
    }

    public void addCommentToTask(Long taskId, CommentRequest commentRequest, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        User user = userService.getByUsername(username);

        Comment comment = new Comment();
        comment.setText(commentRequest.getText());
        comment.setUser(user);
        comment.setTask(task);

        task.getComments().add(comment);
        taskRepository.save(task);
    }
}
