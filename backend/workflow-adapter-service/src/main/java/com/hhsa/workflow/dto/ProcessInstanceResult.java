package com.hhsa.workflow.dto;

import java.util.Map;

/**
 * Result of starting a process instance
 */
public class ProcessInstanceResult {

    private Long processInstanceKey;
    private String processDefinitionKey;
    private Integer processDefinitionVersion;
    private String bpmnProcessId;
    private Map<String, Object> variables;

    public ProcessInstanceResult() {
    }

    public ProcessInstanceResult(Long processInstanceKey, String processDefinitionKey, Integer processDefinitionVersion, String bpmnProcessId) {
        this.processInstanceKey = processInstanceKey;
        this.processDefinitionKey = processDefinitionKey;
        this.processDefinitionVersion = processDefinitionVersion;
        this.bpmnProcessId = bpmnProcessId;
    }

    // Getters and Setters

    public Long getProcessInstanceKey() {
        return processInstanceKey;
    }

    public void setProcessInstanceKey(Long processInstanceKey) {
        this.processInstanceKey = processInstanceKey;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public Integer getProcessDefinitionVersion() {
        return processDefinitionVersion;
    }

    public void setProcessDefinitionVersion(Integer processDefinitionVersion) {
        this.processDefinitionVersion = processDefinitionVersion;
    }

    public String getBpmnProcessId() {
        return bpmnProcessId;
    }

    public void setBpmnProcessId(String bpmnProcessId) {
        this.bpmnProcessId = bpmnProcessId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}




