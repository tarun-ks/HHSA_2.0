package com.hhsa.workflow.dto;

import java.time.LocalDateTime;

/**
 * DTO for activity instance in process history
 */
public class ActivityInstanceDTO {

    private Long activityInstanceKey;
    private String activityId;
    private String activityName;
    private String activityType; // USER_TASK, SERVICE_TASK, etc.
    private String state; // ACTIVE, COMPLETED, TERMINATED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String assignee;

    public ActivityInstanceDTO() {
    }

    // Getters and Setters

    public Long getActivityInstanceKey() {
        return activityInstanceKey;
    }

    public void setActivityInstanceKey(Long activityInstanceKey) {
        this.activityInstanceKey = activityInstanceKey;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}




