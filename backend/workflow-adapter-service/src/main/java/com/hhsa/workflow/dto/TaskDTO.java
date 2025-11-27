package com.hhsa.workflow.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Task DTO for workflow tasks
 */
public class TaskDTO {

    private Long taskKey;
    private String taskType;
    private String taskId;
    private Long processInstanceKey;
    private String processDefinitionId;
    private String assignee;
    private String candidateUser;
    private String candidateGroup;
    private LocalDateTime creationTime;
    private LocalDateTime dueDate;
    private Map<String, Object> variables;
    private String state;

    public TaskDTO() {
    }

    // Getters and Setters

    public Long getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(Long taskKey) {
        this.taskKey = taskKey;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getProcessInstanceKey() {
        return processInstanceKey;
    }

    public void setProcessInstanceKey(Long processInstanceKey) {
        this.processInstanceKey = processInstanceKey;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getCandidateUser() {
        return candidateUser;
    }

    public void setCandidateUser(String candidateUser) {
        this.candidateUser = candidateUser;
    }

    public String getCandidateGroup() {
        return candidateGroup;
    }

    public void setCandidateGroup(String candidateGroup) {
        this.candidateGroup = candidateGroup;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}




