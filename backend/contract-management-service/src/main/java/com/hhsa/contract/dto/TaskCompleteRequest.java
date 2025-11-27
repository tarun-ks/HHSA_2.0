package com.hhsa.contract.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Request DTO for completing a workflow task
 */
public class TaskCompleteRequest {

    @NotNull(message = "Task ID is required")
    private Long taskId;

    private Map<String, Object> variables;

    private String comments; // Optional comments for approval/rejection
    private Boolean approved; // true for approval, false for rejection

    // Getters and Setters

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}


