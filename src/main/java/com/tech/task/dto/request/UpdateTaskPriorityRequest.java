package com.tech.task.dto.request;

import com.tech.task.model.state.Priority;

public class UpdateTaskPriorityRequest {
    private Priority priority;

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
