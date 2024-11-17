package com.tech.task.service.impl;

import com.tech.task.dto.request.CreateOrUpdateTaskRequest;
import com.tech.task.dto.request.CommentRequest;
import com.tech.task.dto.request.UpdateTaskPriorityRequest;
import com.tech.task.dto.request.UpdateTaskStatusRequest;
import com.tech.task.dto.response.TaskResponse;
import com.tech.task.exception.TaskNotFoundException;
import com.tech.task.model.Task;
import com.tech.task.model.User;
import com.tech.task.model.role.Role;
import com.tech.task.model.role.RoleEnum;
import com.tech.task.model.state.Priority;
import com.tech.task.model.state.Status;
import com.tech.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User user;
    private Task task;
    private CreateOrUpdateTaskRequest createTaskRequest;
    private CommentRequest commentRequest;
    private UpdateTaskPriorityRequest updateTaskPriorityRequest;
    private UpdateTaskStatusRequest updateTaskStatusRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setRoles(Collections.singletonList(new Role(RoleEnum.ROLE_USER)));

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setCreator(user);
        task.setExecutors(new ArrayList<>(Collections.singletonList(user)));
        task.setComments(new ArrayList<>());

        createTaskRequest = new CreateOrUpdateTaskRequest();
        createTaskRequest.setTitle("New Task");
        createTaskRequest.setDescription("New Description");
        createTaskRequest.setExecutorsIds(Collections.singletonList(1L));

        commentRequest = new CommentRequest();
        commentRequest.setText("Test Comment");

        updateTaskPriorityRequest = new UpdateTaskPriorityRequest();
        updateTaskPriorityRequest.setPriority(Priority.HIGH);

        updateTaskStatusRequest = new UpdateTaskStatusRequest();
        updateTaskStatusRequest.setStatus(Status.COMPLETED);
    }

    @Test
    void testGetAllTask() {
        when(taskRepository.findAll()).thenReturn(Collections.singletonList(task));
        when(modelMapper.map(any(Task.class), eq(TaskResponse.class))).thenReturn(new TaskResponse());

        List<TaskResponse> result = taskService.getAllTask();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testUpdateTask() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userService.getById(anyLong())).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.updateTask(1L, createTaskRequest);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTask_TaskNotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1L, createTaskRequest));
    }

    @Test
    void testDeleteTask() {
        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetTaskByExecutorId() {
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task));
        when(taskRepository.getTaskByExecutorsId(anyLong(), any(Pageable.class))).thenReturn(taskPage);
        when(modelMapper.map(any(Task.class), eq(TaskResponse.class))).thenReturn(new TaskResponse());

        Page<TaskResponse> result = taskService.getTaskByExecutorId(1L, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetTaskByCreator() {
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task));
        when(taskRepository.getTaskByCreatorId(anyLong(), any(Pageable.class))).thenReturn(taskPage);
        when(modelMapper.map(any(Task.class), eq(TaskResponse.class))).thenReturn(new TaskResponse());

        Page<TaskResponse> result = taskService.getTaskByCreator(1L, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetTaskById() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(modelMapper.map(any(Task.class), eq(TaskResponse.class))).thenReturn(new TaskResponse());

        TaskResponse result = taskService.getTaskById(1L);

        assertNotNull(result);
    }

    @Test
    void testGetTaskById_TaskNotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void testAddCommentToTask() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userService.getByUsername(anyString())).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.addCommentToTask(1L, commentRequest, "testuser");

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testDeleteCommentById() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.deleteCommentById(1L, Collections.singletonList(1L));

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testDeleteExecutorsByIds() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userService.getById(anyLong())).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.deleteExecutorsByIds(1L, Collections.singletonList(1L));

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testAssignExecutors() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userService.getById(anyLong())).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.assignExecutors(1L, Collections.singletonList(1L));

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTaskPriority() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.updateTaskPriority(1L, updateTaskPriorityRequest);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTaskStatus() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userService.getByUsername(anyString())).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.updateTaskStatus(1L, updateTaskStatusRequest, "testuser");

        verify(taskRepository, times(1)).save(any(Task.class));
    }
}
