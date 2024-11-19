package com.tech.task.dto.response;

import com.tech.task.dto.TaskBase;

import java.util.List;

public class TaskResponse extends TaskBase {
    private Long id;
    private UserResponse creator;
    private List<UserResponse> executors;
    private List<CommentResponse> comments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserResponse getCreator() {
        return creator;
    }

    public void setCreator(UserResponse creator) {
        this.creator = creator;
    }

    public List<UserResponse> getExecutors() {
        return executors;
    }

    public void setExecutors(List<UserResponse> executors) {
        this.executors = executors;
    }

    public List<CommentResponse> getComments() {
        return comments;
    }

    public void setComments(List<CommentResponse> comments) {
        this.comments = comments;
    }
}