package com.tech.task.service.impl;

import com.tech.task.dto.request.CreateTaskRequest;
import com.tech.task.model.Task;
import com.tech.task.model.User;
import com.tech.task.repository.TaskRepository;
import com.tech.task.service.TaskService;
import com.tech.task.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserServiceImpl userService;

    public TaskServiceImpl(TaskRepository taskRepository, UserServiceImpl userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public void createTask(CreateTaskRequest createTaskRequest, String username){

        User user = userService.getByUsername(username);

        List<User> executors = createTaskRequest.getExecutorsIds().stream()
                .map(userService::getById)
                .toList();

        Task task = new Task();
        task.setTitle(createTaskRequest.getTitle());
        task.setDescription(createTaskRequest.getDescription());
        task.setPriority(createTaskRequest.getPriority());
        task.setStatus(createTaskRequest.getStatus());
        task.setComment(createTaskRequest.getComment());
        task.setCreator(user);
        task.setExecutors(executors);
        taskRepository.save(task);
    }

    public void deleteTask(Long taskId){
        taskRepository.deleteById(taskId);
    }

}
