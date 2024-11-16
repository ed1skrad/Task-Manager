package com.tech.task.repository;

import com.tech.task.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> getTaskByCreatorId(Long id);
    List<Task> getTaskByExecutorsId(Long id);
}
