package com.tech.task.dto.request;

import com.tech.task.model.state.Priority;
import com.tech.task.model.state.Status;

import java.util.List;

public class CreateOrUpdateTaskRequest {
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private List<Long> executorsIds;
    private List<CommentRequest> comments;

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

    public List<Long> getExecutorsIds() {
        return executorsIds;
    }

    public void setExecutorsIds(List<Long> executorsIds) {
        this.executorsIds = executorsIds;
    }

    public List<CommentRequest> getComments() {
        return comments;
    }

    public void setComments(List<CommentRequest> comments) {
        this.comments = comments;
    }
}
