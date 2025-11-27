package com.hhsa.workflow.adapter;

import com.hhsa.workflow.dto.*;

import java.util.List;
import java.util.Map;

/**
 * Workflow adapter interface for abstracting workflow engine implementation.
 * This allows swapping Camunda 8 for other workflow engines without breaking consumers.
 */
public interface WorkflowAdapter {

    /**
     * Deploy BPMN process definition
     * @param bpmnXml BPMN XML content
     * @return Deployment result with process definition key
     */
    DeploymentResult deployProcess(String bpmnXml) throws WorkflowException;

    /**
     * Start a process instance
     * @param processDefinitionKey Process definition key (BPMN process ID)
     * @param variables Process variables
     * @return Process instance with instance key
     */
    ProcessInstanceResult startProcess(String processDefinitionKey, Map<String, Object> variables) throws WorkflowException;

    /**
     * Complete a user task
     * @param taskId Task ID
     * @param variables Task variables
     */
    void completeTask(Long taskId, Map<String, Object> variables) throws WorkflowException;

    /**
     * Get tasks for a user
     * @param userId User ID
     * @return List of tasks
     */
    List<TaskDTO> getTasksForUser(String userId) throws WorkflowException;

    /**
     * Get tasks by process instance
     * @param processInstanceKey Process instance key
     * @return List of tasks
     */
    List<TaskDTO> getTasksByProcessInstance(Long processInstanceKey) throws WorkflowException;

    /**
     * Get process instance details
     * @param processInstanceKey Process instance key
     * @return Process instance details
     */
    ProcessInstanceDTO getProcessInstance(Long processInstanceKey) throws WorkflowException;

    /**
     * Cancel a process instance
     * @param processInstanceKey Process instance key
     */
    void cancelProcessInstance(Long processInstanceKey) throws WorkflowException;

    /**
     * Evaluate a DMN decision
     * @param decisionId DMN decision ID
     * @param variables Decision variables
     * @return Decision result
     */
    DecisionResult evaluateDecision(String decisionId, Map<String, Object> variables) throws WorkflowException;

    /**
     * Get process instance history
     * @param contractId Contract ID to find process instances
     * @return Process instance history
     */
    ProcessInstanceHistoryDTO getProcessInstanceHistory(Long contractId) throws WorkflowException;

    /**
     * Assign a task to a user
     * @param taskId Task ID
     * @param userId User ID to assign to
     */
    void assignTask(Long taskId, String userId) throws WorkflowException;

    /**
     * Claim an unassigned task
     * @param taskId Task ID
     * @param userId User ID claiming the task
     */
    void claimTask(Long taskId, String userId) throws WorkflowException;

    /**
     * Unclaim a task (make it available for others)
     * @param taskId Task ID
     */
    void unclaimTask(Long taskId) throws WorkflowException;

    /**
     * Reassign a task to a different user
     * @param taskId Task ID
     * @param userId New user ID
     */
    void reassignTask(Long taskId, String userId) throws WorkflowException;

    /**
     * Return a task to the previous step in the workflow
     * @param taskId Task ID
     * @param reason Reason for return
     */
    void returnTask(Long taskId, String reason) throws WorkflowException;

    /**
     * Get flow node instances (activities) for a process instance
     * Used for visualizing workflow status in BPMN diagrams
     * @param processInstanceKey Process instance key
     * @return List of activity instances with their states (empty list if unavailable)
     */
    java.util.List<com.hhsa.workflow.dto.ActivityInstanceDTO> getFlowNodeInstances(Long processInstanceKey) throws WorkflowException;

    /**
     * Get incidents (errors) for a process instance
     * Used for visualizing errors in BPMN diagrams
     * @param processInstanceKey Process instance key
     * @return List of incidents (empty list if unavailable)
     */
    java.util.List<com.hhsa.workflow.dto.IncidentDTO> getIncidents(Long processInstanceKey) throws WorkflowException;
}

