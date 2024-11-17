package com.tech.task.dto.request;

import com.tech.task.dto.TaskBase;

import java.util.List;

public class CreateOrUpdateTaskRequest extends TaskBase {
    private List<CommentRequest> comments;

    public List<CommentRequest> getComments() {
        return comments;
    }

    public void setComments(List<CommentRequest> comments) {
        this.comments = comments;
    }
}
