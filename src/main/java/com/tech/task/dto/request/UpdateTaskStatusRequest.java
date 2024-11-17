package com.tech.task.dto.request;

import com.tech.task.model.state.Status;

public class UpdateTaskStatusRequest {
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
