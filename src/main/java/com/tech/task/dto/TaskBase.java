package com.tech.task.dto;

import com.tech.task.model.state.Priority;
import com.tech.task.model.state.Status;

import java.util.List;

public abstract class TaskBase {
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private List<Long> executorsIds;

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

    public Priority getPriority() {
        return priority;
    }

    public List<Long> getExecutorsIds() {
        return executorsIds;
    }

    public void setExecutorsIds(List<Long> executorsIds) {
        this.executorsIds = executorsIds;
    }
}
