package com.hhsa.workflow.service;

import com.hhsa.common.core.dto.ApiResponse;
import com.hhsa.workflow.adapter.WorkflowAdapter;
import com.hhsa.workflow.adapter.WorkflowException;
import com.hhsa.workflow.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Workflow service providing high-level workflow operations.
 * Delegates to WorkflowAdapter for actual implementation.
 */
@Service
public class WorkflowService {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowService.class);

    private final WorkflowAdapter workflowAdapter;
    private final BpmnDeploymentService bpmnDeploymentService;

    public WorkflowService(WorkflowAdapter workflowAdapter, BpmnDeploymentService bpmnDeploymentService) {
        this.workflowAdapter = workflowAdapter;
        this.bpmnDeploymentService = bpmnDeploymentService;
    }

    /**
     * Deploy BPMN process
     */
    public DeploymentResult deployProcess(String bpmnXml) throws WorkflowException {
        logger.debug("Deploying BPMN process");
        return workflowAdapter.deployProcess(bpmnXml);
    }

    /**
     * Start process instance
     */
    public ProcessInstanceResult startProcess(String processDefinitionKey, Map<String, Object> variables) throws WorkflowException {
        logger.debug("Starting process: {}", processDefinitionKey);
        return workflowAdapter.startProcess(processDefinitionKey, variables);
    }

    /**
     * Complete task
     */
    public void completeTask(Long taskId, Map<String, Object> variables) throws WorkflowException {
        logger.debug("Completing task: {}", taskId);
        workflowAdapter.completeTask(taskId, variables);
    }

    /**
     * Get tasks for user
     */
    public List<TaskDTO> getTasksForUser(String userId) throws WorkflowException {
        logger.debug("Getting tasks for user: {}", userId);
        return workflowAdapter.getTasksForUser(userId);
    }

    /**
     * Get tasks by process instance
     */
    public List<TaskDTO> getTasksByProcessInstance(Long processInstanceKey) throws WorkflowException {
        logger.debug("Getting tasks for process instance: {}", processInstanceKey);
        return workflowAdapter.getTasksByProcessInstance(processInstanceKey);
    }

    /**
     * Get process instance
     */
    public ProcessInstanceDTO getProcessInstance(Long processInstanceKey) throws WorkflowException {
        logger.debug("Getting process instance: {}", processInstanceKey);
        return workflowAdapter.getProcessInstance(processInstanceKey);
    }

    /**
     * Cancel process instance
     */
    public void cancelProcessInstance(Long processInstanceKey) throws WorkflowException {
        logger.debug("Cancelling process instance: {}", processInstanceKey);
        workflowAdapter.cancelProcessInstance(processInstanceKey);
    }

    /**
     * Evaluate DMN decision
     */
    public DecisionResult evaluateDecision(String decisionId, Map<String, Object> variables) throws WorkflowException {
        logger.debug("Evaluating DMN decision: {}", decisionId);
        return workflowAdapter.evaluateDecision(decisionId, variables);
    }

    /**
     * Get process instance history for a contract
     */
    public ProcessInstanceHistoryDTO getProcessInstanceHistory(Long contractId) throws WorkflowException {
        logger.debug("Getting process instance history for contract: {}", contractId);
        return workflowAdapter.getProcessInstanceHistory(contractId);
    }

    /**
     * Get flow node instances (activities) for a process instance
     * Used for visualizing workflow status in BPMN diagrams
     */
    public List<ActivityInstanceDTO> getFlowNodeInstances(Long processInstanceKey) throws WorkflowException {
        try {
            logger.debug("Getting flow node instances for process instance: {}", processInstanceKey);
            List<ActivityInstanceDTO> result = workflowAdapter.getFlowNodeInstances(processInstanceKey);
            return result != null ? result : new java.util.ArrayList<>();
        } catch (Exception e) {
            logger.warn("Failed to get flow node instances for process instance: {} - {}", processInstanceKey, e.getMessage());
            // Return empty list for graceful degradation
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Get incidents (errors) for a process instance
     * Used for visualizing errors in BPMN diagrams
     */
    public List<IncidentDTO> getIncidents(Long processInstanceKey) throws WorkflowException {
        try {
            logger.debug("Getting incidents for process instance: {}", processInstanceKey);
            List<IncidentDTO> result = workflowAdapter.getIncidents(processInstanceKey);
            return result != null ? result : new java.util.ArrayList<>();
        } catch (Exception e) {
            logger.warn("Failed to get incidents for process instance: {} - {}", processInstanceKey, e.getMessage());
            // Return empty list for graceful degradation
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Get task history for a process instance
     * Returns detailed timeline of task creation, assignment, and completion/approval
     */
    public List<com.hhsa.workflow.dto.TaskHistoryDTO> getTaskHistory(Long processInstanceKey) throws WorkflowException {
        try {
            logger.debug("Getting task history for process instance: {}", processInstanceKey);
            if (workflowAdapter instanceof com.hhsa.workflow.adapter.Camunda8WorkflowAdapter) {
                com.hhsa.workflow.adapter.Camunda8WorkflowAdapter camundaAdapter = 
                    (com.hhsa.workflow.adapter.Camunda8WorkflowAdapter) workflowAdapter;
                return camundaAdapter.getTaskHistory(processInstanceKey);
            }
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            logger.warn("Failed to get task history for process instance: {} - {}", processInstanceKey, e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Get BPMN XML for a process definition
     * Framework-based: works for any workflow by process definition key
     */
    public String getBpmnXml(String processDefinitionKey) throws WorkflowException {
        logger.debug("Getting BPMN XML for process definition: {}", processDefinitionKey);
        return bpmnDeploymentService.getBpmnXml(processDefinitionKey);
    }

    /**
     * Assign a task to a user
     */
    public void assignTask(Long taskId, String userId) throws WorkflowException {
        logger.debug("Assigning task: {} to user: {}", taskId, userId);
        workflowAdapter.assignTask(taskId, userId);
    }

    /**
     * Claim an unassigned task
     */
    public void claimTask(Long taskId, String userId) throws WorkflowException {
        logger.debug("Claiming task: {} by user: {}", taskId, userId);
        workflowAdapter.claimTask(taskId, userId);
    }

    /**
     * Unclaim a task
     */
    public void unclaimTask(Long taskId) throws WorkflowException {
        logger.debug("Unclaiming task: {}", taskId);
        workflowAdapter.unclaimTask(taskId);
    }

    /**
     * Reassign a task to a different user
     */
    public void reassignTask(Long taskId, String userId) throws WorkflowException {
        logger.debug("Reassigning task: {} to user: {}", taskId, userId);
        workflowAdapter.reassignTask(taskId, userId);
    }

    /**
     * Return a task to the previous step
     */
    public void returnTask(Long taskId, String reason) throws WorkflowException {
        logger.debug("Returning task: {} with reason: {}", taskId, reason);
        workflowAdapter.returnTask(taskId, reason);
    }
}

