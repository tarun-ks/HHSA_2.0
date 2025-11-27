package com.hhsa.workflow.dto;

import java.time.LocalDateTime;

/**
 * DTO for task history with detailed timeline information
 * Shows task creation, assignment, and completion/approval details
 */
public class TaskHistoryDTO {

    private Long taskKey;
    private String taskId;
    private String taskName;
    private String taskType;
    private Long processInstanceKey;
    private String processDefinitionId;
    
    // Timeline information
    private LocalDateTime creationTime;
    private String createdBy; // Process initiator or system
    private LocalDateTime assignmentTime;
    private String assignedTo; // User who was assigned
    private LocalDateTime claimedTime;
    private String claimedBy; // User who claimed the task
    private LocalDateTime completionTime;
    private String completedBy; // User who completed/approved
    private LocalDateTime dueDate;
    
    // Status information
    private String state; // CREATED, ASSIGNED, CLAIMED, COMPLETED, CANCELLED
    private String assignee; // Current assignee
    private String candidateUser;
    private String candidateGroup;
    
    // Additional details
    private String completionComment; // Approval/rejection comment
    private String outcome; // APPROVED, REJECTED, etc.

    public TaskHistoryDTO() {
    }

    // Getters and Setters

    public Long getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(Long taskKey) {
        this.taskKey = taskKey;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
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

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getAssignmentTime() {
        return assignmentTime;
    }

    public void setAssignmentTime(LocalDateTime assignmentTime) {
        this.assignmentTime = assignmentTime;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDateTime getClaimedTime() {
        return claimedTime;
    }

    public void setClaimedTime(LocalDateTime claimedTime) {
        this.claimedTime = claimedTime;
    }

    public String getClaimedBy() {
        return claimedBy;
    }

    public void setClaimedBy(String claimedBy) {
        this.claimedBy = claimedBy;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getCompletionComment() {
        return completionComment;
    }

    public void setCompletionComment(String completionComment) {
        this.completionComment = completionComment;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
}

