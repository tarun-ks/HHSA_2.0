package com.hhsa.workflow.dto;

import java.time.LocalDateTime;

/**
 * DTO for process incident
 */
public class IncidentDTO {

    private Long incidentKey;
    private String incidentType;
    private String errorMessage;
    private LocalDateTime creationTime;
    private LocalDateTime resolutionTime;
    private String state; // OPEN, RESOLVED

    public IncidentDTO() {
    }

    // Getters and Setters

    public Long getIncidentKey() {
        return incidentKey;
    }

    public void setIncidentKey(Long incidentKey) {
        this.incidentKey = incidentKey;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getResolutionTime() {
        return resolutionTime;
    }

    public void setResolutionTime(LocalDateTime resolutionTime) {
        this.resolutionTime = resolutionTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}




