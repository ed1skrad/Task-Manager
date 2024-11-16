package com.tech.task.dto;

import com.tech.task.model.state.Priority;
import com.tech.task.model.state.Status;

import java.util.List;

public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private String comment;
    private UserDTO creator;
    private List<UserDTO> executors;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserDTO getCreator() {
        return creator;
    }

    public void setCreator(UserDTO creator) {
        this.creator = creator;
    }

    public List<UserDTO> getExecutors() {
        return executors;
    }

    public void setExecutors(List<UserDTO> executors) {
        this.executors = executors;
    }
}
