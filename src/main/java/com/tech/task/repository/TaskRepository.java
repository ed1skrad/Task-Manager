package com.tech.task.repository;

import com.tech.task.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    Page<Task> getTaskByExecutorsId(Long id, Pageable pageable);

    Page<Task> getTaskByCreatorId(Long id, Pageable pageable);

}
