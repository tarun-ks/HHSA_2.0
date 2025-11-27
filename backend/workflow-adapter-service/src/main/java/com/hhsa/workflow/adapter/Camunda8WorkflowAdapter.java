package com.hhsa.workflow.adapter;

import com.hhsa.workflow.dto.*;
import com.hhsa.workflow.service.OperateApiService;
import java.util.ArrayList;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Camunda 8 (Zeebe) workflow adapter implementation.
 * Provides BPMN and DMN integration with Zeebe.
 */
@Component("camunda8WorkflowAdapter")
public class Camunda8WorkflowAdapter implements WorkflowAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Camunda8WorkflowAdapter.class);

    private final ZeebeClient zeebeClient;
    private final OperateApiService operateApiService;

    public Camunda8WorkflowAdapter(ZeebeClient zeebeClient, OperateApiService operateApiService) {
        this.zeebeClient = zeebeClient;
        this.operateApiService = operateApiService;
    }

    @Override
    public DeploymentResult deployProcess(String bpmnXml) throws WorkflowException {
        try {
            logger.debug("Deploying BPMN process");

            DeploymentEvent deploymentEvent = zeebeClient.newDeployResourceCommand()
                .addResourceString(bpmnXml, java.nio.charset.StandardCharsets.UTF_8, "process.bpmn")
                .send()
                .join();

            // Extract process definition from deployment
            io.camunda.zeebe.client.api.response.Process process = deploymentEvent.getProcesses().get(0);
            
            DeploymentResult result = new DeploymentResult(
                deploymentEvent.getKey(),
                String.valueOf(process.getProcessDefinitionKey()),
                process.getBpmnProcessId(),
                process.getVersion()
            );

            logger.info("BPMN process deployed successfully: {} (key: {})", 
                process.getBpmnProcessId(), process.getProcessDefinitionKey());
            
            return result;

        } catch (Exception e) {
            logger.error("Failed to deploy BPMN process", e);
            throw new WorkflowException("Failed to deploy BPMN process: " + e.getMessage(), e);
        }
    }

    @Override
    public com.hhsa.workflow.dto.ProcessInstanceResult startProcess(String processDefinitionKey, Map<String, Object> variables) throws WorkflowException {
        try {
            logger.debug("Starting process instance: {} with variables: {}", processDefinitionKey, variables);

            ProcessInstanceEvent event;
            // Zeebe 8.6.0 API: Step2 only has version() and latestVersion() methods
            // We need to go to Step3 first, which has variables() and send()
            io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3 step3 = 
                zeebeClient.newCreateInstanceCommand()
                    .bpmnProcessId(processDefinitionKey)
                    .latestVersion(); // Use latest version of the process
            
            if (variables != null && !variables.isEmpty()) {
                event = step3.variables(variables).send().join();
            } else {
                event = step3.send().join();
            }

            com.hhsa.workflow.dto.ProcessInstanceResult result = new com.hhsa.workflow.dto.ProcessInstanceResult(
                event.getProcessInstanceKey(),
                String.valueOf(event.getProcessDefinitionKey()),
                1, // Version - ProcessInstanceEvent doesn't have getProcessDefinitionVersion()
                event.getBpmnProcessId()
            );
            result.setVariables(variables);

            logger.info("Process instance started: {} (instance key: {})", 
                processDefinitionKey, event.getProcessInstanceKey());
            
            return result;

        } catch (Exception e) {
            logger.error("Failed to start process instance: {}", processDefinitionKey, e);
            throw new WorkflowException("Failed to start process instance: " + e.getMessage(), e);
        }
    }

    @Override
    public void completeTask(Long taskId, Map<String, Object> variables) throws WorkflowException {
        try {
            logger.debug("Completing task: {} with variables: {}", taskId, variables);

            // Try to complete via Operate API first (for user tasks)
            try {
                operateApiService.completeTask(taskId, variables);
                logger.info("Task completed successfully via Operate API: {}", taskId);
                return;
            } catch (Exception operateEx) {
                logger.debug("Failed to complete via Operate API, trying Zeebe client directly: {}", operateEx.getMessage());
                // If Operate API fails, try using Zeebe client directly (for jobs)
                // This handles cases where tasks are jobs in Zeebe
                completeJobDirectly(taskId, variables);
                logger.info("Task completed successfully via Zeebe client: {}", taskId);
            }

        } catch (Exception e) {
            logger.error("Failed to complete task: {}", taskId, e);
            throw new WorkflowException("Failed to complete task: " + e.getMessage(), e);
        }
    }

    /**
     * Complete a job directly using Zeebe client
     * Used as fallback when Operate API is not available
     */
    private void completeJobDirectly(Long jobKey, Map<String, Object> variables) throws WorkflowException {
        try {
            logger.debug("Completing job: {} directly via Zeebe client", jobKey);

            io.camunda.zeebe.client.api.ZeebeFuture<CompleteJobResponse> future = zeebeClient.newCompleteCommand(jobKey)
                .variables(variables != null ? variables : new HashMap<>())
                .send();

            CompleteJobResponse response = future.join();
            logger.info("Job completed successfully: {}", jobKey);

        } catch (Exception e) {
            logger.error("Failed to complete job via Zeebe client: {}", jobKey, e);
            throw new WorkflowException("Failed to complete job: " + e.getMessage(), e);
        }
    }

    /**
     * Activate and complete a job for a process instance
     * Used for testing - activates the first available job and completes it
     */
    public void activateAndCompleteJobForProcessInstance(Long processInstanceKey, String jobType) throws WorkflowException {
        try {
            logger.info("Activating and completing job for process instance: {} (job type: {})", processInstanceKey, jobType);

            // Activate jobs for the process instance
            // Note: In Zeebe, we need to activate jobs first before completing them
            // This is a simplified approach - in production, use job workers
            
            // For user tasks, we typically need to use Tasklist API
            // But for service tasks, we can activate and complete jobs directly
            // This method is mainly for testing purposes
            
            logger.warn("Direct job activation/completion not fully implemented. Use Tasklist API for user tasks.");
            throw new WorkflowException("Please use Tasklist API or Operate API to complete user tasks");

        } catch (WorkflowException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to activate and complete job for process instance: {}", processInstanceKey, e);
            throw new WorkflowException("Failed to activate and complete job: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TaskDTO> getTasksForUser(String userId) throws WorkflowException {
        try {
            logger.debug("Getting tasks for user: {} from Operate API", userId);

            // Use Operate API to get user tasks
            List<TaskDTO> tasks = operateApiService.getTasksForUser(userId);
            
            logger.info("Found {} tasks for user: {}", tasks.size(), userId);
            return tasks;

        } catch (Exception e) {
            logger.error("Failed to get tasks for user: {}", userId, e);
            throw new WorkflowException("Failed to get tasks for user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TaskDTO> getTasksByProcessInstance(Long processInstanceKey) throws WorkflowException {
        try {
            logger.debug("Getting tasks for process instance: {} from Operate API", processInstanceKey);

            // Use Operate API to get tasks for process instance
            List<TaskDTO> tasks = operateApiService.getTasksByProcessInstance(processInstanceKey);
            
            return tasks;

        } catch (Exception e) {
            logger.error("Failed to get tasks for process instance: {}", processInstanceKey, e);
            throw new WorkflowException("Failed to get tasks for process instance: " + e.getMessage(), e);
        }
    }

    @Override
    public ProcessInstanceDTO getProcessInstance(Long processInstanceKey) throws WorkflowException {
        try {
            logger.debug("Getting process instance: {} from Operate API", processInstanceKey);

            // Use Operate API to get process instance details
            Map<String, Object> processInstanceData = operateApiService.getProcessInstance(processInstanceKey);
            
            ProcessInstanceDTO dto = new ProcessInstanceDTO();
            dto.setProcessInstanceKey(processInstanceKey);
            
            if (processInstanceData.containsKey("bpmnProcessId")) {
                dto.setBpmnProcessId(processInstanceData.get("bpmnProcessId").toString());
            }
            if (processInstanceData.containsKey("processDefinitionKey")) {
                dto.setProcessDefinitionKey(Long.valueOf(processInstanceData.get("processDefinitionKey").toString()));
            }
            if (processInstanceData.containsKey("state")) {
                dto.setState(processInstanceData.get("state").toString());
            }
            if (processInstanceData.containsKey("startDate")) {
                // Parse start date
                dto.setStartTime(LocalDateTime.now()); // Simplified
            }
            if (processInstanceData.containsKey("endDate")) {
                dto.setEndTime(LocalDateTime.now()); // Simplified
            }
            
            return dto;

        } catch (Exception e) {
            logger.error("Failed to get process instance: {}", processInstanceKey, e);
            throw new WorkflowException("Failed to get process instance: " + e.getMessage(), e);
        }
    }

    @Override
    public void cancelProcessInstance(Long processInstanceKey) throws WorkflowException {
        try {
            logger.debug("Cancelling process instance: {}", processInstanceKey);

            zeebeClient.newCancelInstanceCommand(processInstanceKey)
                .send()
                .join();

            logger.info("Process instance cancelled: {}", processInstanceKey);

        } catch (Exception e) {
            logger.error("Failed to cancel process instance: {}", processInstanceKey, e);
            throw new WorkflowException("Failed to cancel process instance: " + e.getMessage(), e);
        }
    }

    @Override
    public DecisionResult evaluateDecision(String decisionId, Map<String, Object> variables) throws WorkflowException {
        try {
            logger.debug("Evaluating DMN decision: {} with variables: {}", decisionId, variables);

            // Note: DMN evaluation in Zeebe requires:
            // 1. DMN deployed to Zeebe
            // 2. Using EvaluateDecisionCommand
            // This is a placeholder implementation
            
            logger.warn("DMN decision evaluation not fully implemented. Use Zeebe EvaluateDecisionCommand.");
            
            DecisionResult result = new DecisionResult();
            result.setDecisionId(decisionId);
            result.setResult(Collections.emptyMap());
            return result;

        } catch (Exception e) {
            logger.error("Failed to evaluate DMN decision: {}", decisionId, e);
            throw new WorkflowException("Failed to evaluate DMN decision: " + e.getMessage(), e);
        }
    }

    @Override
    public ProcessInstanceHistoryDTO getProcessInstanceHistory(Long contractId) throws WorkflowException {
        try {
            logger.debug("Getting process instance history for contract: {}", contractId);

            // Query Operate API for process instances with contractId variable
            // This would require querying by process variables
            // For now, return a simplified history
            
            ProcessInstanceHistoryDTO history = new ProcessInstanceHistoryDTO();
            // Initialize with empty lists - Lombok @Data generates setters automatically
            // Lists will be initialized as empty by default
            
            // In production, query Operate API for process instances and activities
            // Map to ProcessInstanceHistoryDTO
            
            return history;

        } catch (Exception e) {
            logger.error("Failed to get process instance history for contract: {}", contractId, e);
            throw new WorkflowException("Failed to get process instance history: " + e.getMessage(), e);
        }
    }

    @Override
    public void assignTask(Long taskId, String userId) throws WorkflowException {
        try {
            logger.debug("Assigning task: {} to user: {}", taskId, userId);
            operateApiService.assignTask(taskId, userId);
            logger.info("Task assigned successfully: {} to user: {}", taskId, userId);
        } catch (Exception e) {
            logger.error("Failed to assign task: {}", taskId, e);
            throw new WorkflowException("Failed to assign task: " + e.getMessage(), e);
        }
    }

    @Override
    public void claimTask(Long taskId, String userId) throws WorkflowException {
        try {
            logger.debug("Claiming task: {} by user: {}", taskId, userId);
            operateApiService.claimTask(taskId, userId);
            logger.info("Task claimed successfully: {} by user: {}", taskId, userId);
        } catch (Exception e) {
            logger.error("Failed to claim task: {}", taskId, e);
            throw new WorkflowException("Failed to claim task: " + e.getMessage(), e);
        }
    }

    @Override
    public void unclaimTask(Long taskId) throws WorkflowException {
        try {
            logger.debug("Unclaiming task: {}", taskId);
            operateApiService.unclaimTask(taskId);
            logger.info("Task unclaimed successfully: {}", taskId);
        } catch (Exception e) {
            logger.error("Failed to unclaim task: {}", taskId, e);
            throw new WorkflowException("Failed to unclaim task: " + e.getMessage(), e);
        }
    }

    @Override
    public void reassignTask(Long taskId, String userId) throws WorkflowException {
        try {
            logger.debug("Reassigning task: {} to user: {}", taskId, userId);
            // First unclaim, then assign to new user
            operateApiService.unclaimTask(taskId);
            operateApiService.assignTask(taskId, userId);
            logger.info("Task reassigned successfully: {} to user: {}", taskId, userId);
        } catch (Exception e) {
            logger.error("Failed to reassign task: {}", taskId, e);
            throw new WorkflowException("Failed to reassign task: " + e.getMessage(), e);
        }
    }

    @Override
    public void returnTask(Long taskId, String reason) throws WorkflowException {
        try {
            logger.debug("Returning task: {} with reason: {}", taskId, reason);
            // Return task by completing it with a return flag
            // In Camunda 8, we can use a boundary event or complete with return variable
            Map<String, Object> variables = new HashMap<>();
            variables.put("returned", true);
            variables.put("returnReason", reason);
            // Complete the task with return flag - workflow will handle routing
            completeTask(taskId, variables);
            logger.info("Task returned successfully: {}", taskId);
        } catch (Exception e) {
            logger.error("Failed to return task: {}", taskId, e);
            throw new WorkflowException("Failed to return task: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ActivityInstanceDTO> getFlowNodeInstances(Long processInstanceKey) throws WorkflowException {
        try {
            logger.debug("Getting flow node instances for process instance: {}", processInstanceKey);
            List<ActivityInstanceDTO> result = operateApiService.getFlowNodeInstances(processInstanceKey);
            // OperateApiService already returns empty list on error, so just return it
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            logger.warn("Failed to get flow node instances for process instance: {} - {}", processInstanceKey, e.getMessage());
            // Return empty list instead of throwing - allows graceful degradation
            return new ArrayList<>();
        }
    }

    @Override
    public List<IncidentDTO> getIncidents(Long processInstanceKey) throws WorkflowException {
        try {
            logger.debug("Getting incidents for process instance: {}", processInstanceKey);
            List<IncidentDTO> result = operateApiService.getIncidents(processInstanceKey);
            // OperateApiService already returns empty list on error, so just return it
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            logger.warn("Failed to get incidents for process instance: {} - {}", processInstanceKey, e.getMessage());
            // Return empty list instead of throwing - allows graceful degradation
            return new ArrayList<>();
        }
    }

    /**
     * Get task history for a process instance
     * Returns detailed timeline of task creation, assignment, and completion
     */
    public List<com.hhsa.workflow.dto.TaskHistoryDTO> getTaskHistory(Long processInstanceKey) throws WorkflowException {
        try {
            logger.debug("Getting task history for process instance: {}", processInstanceKey);
            List<com.hhsa.workflow.dto.TaskHistoryDTO> result = operateApiService.getTaskHistory(processInstanceKey);
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            logger.warn("Failed to get task history for process instance: {} - {}", processInstanceKey, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Convert Instant to LocalDateTime
     */
    private LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}

