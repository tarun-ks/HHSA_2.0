package com.hhsa.workflow.dto;

/**
 * Result of process deployment
 */
public class DeploymentResult {

    private Long deploymentKey;
    private String processDefinitionKey;
    private String processDefinitionId;
    private Integer version;

    public DeploymentResult() {
    }

    public DeploymentResult(Long deploymentKey, String processDefinitionKey, String processDefinitionId, Integer version) {
        this.deploymentKey = deploymentKey;
        this.processDefinitionKey = processDefinitionKey;
        this.processDefinitionId = processDefinitionId;
        this.version = version;
    }

    // Getters and Setters

    public Long getDeploymentKey() {
        return deploymentKey;
    }

    public void setDeploymentKey(Long deploymentKey) {
        this.deploymentKey = deploymentKey;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}




