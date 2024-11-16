package com.tech.task.dto.request;

import com.tech.task.model.User;
import com.tech.task.model.state.Priority;
import com.tech.task.model.state.Status;

import java.util.List;

public class CreateTaskRequest {

    private String title;

    private String description;

    private Status status;

    private Priority priority;

    private String comment;

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

    public List<Long> getExecutorsIds() {
        return executorsIds;
    }

    public void setExecutorsIds(List<Long> executorsIds) {
        this.executorsIds = executorsIds;
    }
}
