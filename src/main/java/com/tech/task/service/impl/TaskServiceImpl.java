package com.tech.task.service.impl;

import com.tech.task.dto.TaskDTO;
import com.tech.task.dto.request.CreateTaskRequest;
import com.tech.task.model.Task;
import com.tech.task.model.User;
import com.tech.task.repository.TaskRepository;
import com.tech.task.service.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<TaskDTO> getAllTask(){
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .toList();
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


    public List<TaskDTO> getTaskByExecutorId(Long id){
        List<Task> tasks = taskRepository.getTaskByExecutorsId(id);
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .toList();
    }

    public TaskDTO getTaskByCreator(Long id) {
        Task task = taskRepository.getTaskByCreatorId(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return modelMapper.map(task, TaskDTO.class);
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() ->  new RuntimeException("Task not found!"));
        return modelMapper.map(task, TaskDTO.class);
    }
}
