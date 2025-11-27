package com.hhsa.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Request DTO for creating audit events
 */
public class AuditEventRequest {

    @NotBlank(message = "Entity type is required")
    private String entityType;

    @NotNull(message = "Entity ID is required")
    private Long entityId;

    @NotBlank(message = "Action is required")
    private String action; // CREATE, UPDATE, DELETE, LOGIN, LOGOUT, WORKFLOW_STATE_CHANGE

    @NotBlank(message = "User ID is required")
    private String userId;

    private String tenantId;
    private Map<String, Object> changes; // Old and new values
    private String ipAddress;
    private String userAgent;
    private String correlationId;

    public AuditEventRequest() {
    }

    // Getters and Setters

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Map<String, Object> getChanges() {
        return changes;
    }

    public void setChanges(Map<String, Object> changes) {
        this.changes = changes;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}




