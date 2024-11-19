package com.tech.task.dto.request;

import com.tech.task.dto.TaskBase;

import java.util.List;

public class CreateOrUpdateTaskRequest extends TaskBase {
    private List<Long> executorsIds;

    public List<Long> getExecutorsIds() {
        return executorsIds;
    }

    public void setExecutorsIds(List<Long> executorsIds) {
        this.executorsIds = executorsIds;
    }
}