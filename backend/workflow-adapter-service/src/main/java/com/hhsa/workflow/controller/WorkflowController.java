package com.hhsa.workflow.controller;

import com.hhsa.common.core.dto.ApiResponse;
import com.hhsa.workflow.adapter.WorkflowException;
import com.hhsa.workflow.dto.*;
import com.hhsa.workflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Workflow controller.
 * Provides REST endpoints for workflow operations (BPMN and DMN).
 */
@RestController
@RequestMapping("/api/v1/workflows")
@Tag(name = "Workflows", description = "Workflow management endpoints (BPMN and DMN)")
public class WorkflowController {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowController.class);

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/deploy")
    @Operation(summary = "Deploy BPMN Process", description = "Deploy a BPMN process definition")
    public ResponseEntity<ApiResponse<DeploymentResult>> deployProcess(@RequestBody String bpmnXml) {
        try {
            DeploymentResult result = workflowService.deployProcess(bpmnXml);
            return ResponseEntity.ok(ApiResponse.success(result, "Process deployed successfully"));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("DEPLOYMENT_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to deploy process: " + e.getMessage(), new ApiResponse.ErrorDetails("DEPLOYMENT_ERROR", "Failed to deploy process: " + e.getMessage())));
        }
    }

    @PostMapping("/processes/{processDefinitionKey}/start")
    @Operation(summary = "Start Process Instance", description = "Start a new process instance")
    public ResponseEntity<ApiResponse<ProcessInstanceResult>> startProcess(
            @PathVariable String processDefinitionKey,
            @RequestBody(required = false) Map<String, Object> variables) {
        try {
            ProcessInstanceResult result = workflowService.startProcess(processDefinitionKey, variables);
            return ResponseEntity.ok(ApiResponse.success(result, "Process instance started successfully"));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("PROCESS_START_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to start process: " + e.getMessage(), new ApiResponse.ErrorDetails("PROCESS_START_ERROR", "Failed to start process: " + e.getMessage())));
        }
    }

    @PostMapping("/tasks/{taskId}/complete")
    @Operation(summary = "Complete Task", description = "Complete a workflow task")
    public ResponseEntity<ApiResponse<Void>> completeTask(
            @PathVariable Long taskId,
            @RequestBody(required = false) Map<String, Object> variables) {
        try {
            workflowService.completeTask(taskId, variables);
            return ResponseEntity.ok(ApiResponse.success(null, "Task completed successfully"));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("TASK_COMPLETE_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to complete task: " + e.getMessage(), new ApiResponse.ErrorDetails("TASK_COMPLETE_ERROR", "Failed to complete task: " + e.getMessage())));
        }
    }

    @GetMapping("/tasks/user/{userId}")
    @Operation(summary = "Get Tasks for User", description = "Get all tasks assigned to a user")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksForUser(@PathVariable String userId) {
        try {
            List<TaskDTO> tasks = workflowService.getTasksForUser(userId);
            return ResponseEntity.ok(ApiResponse.success(tasks));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("TASK_QUERY_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get tasks: " + e.getMessage(), new ApiResponse.ErrorDetails("TASK_QUERY_ERROR", "Failed to get tasks: " + e.getMessage())));
        }
    }

    @GetMapping("/process-instances/{processInstanceKey}/tasks")
    @Operation(summary = "Get Tasks by Process Instance", description = "Get all tasks for a process instance")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByProcessInstance(
            @PathVariable Long processInstanceKey) {
        try {
            List<TaskDTO> tasks = workflowService.getTasksByProcessInstance(processInstanceKey);
            return ResponseEntity.ok(ApiResponse.success(tasks));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("TASK_QUERY_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get tasks: " + e.getMessage(), new ApiResponse.ErrorDetails("TASK_QUERY_ERROR", "Failed to get tasks: " + e.getMessage())));
        }
    }

    @GetMapping("/process-instances/{processInstanceKey}")
    @Operation(summary = "Get Process Instance", description = "Get process instance details")
    public ResponseEntity<ApiResponse<ProcessInstanceDTO>> getProcessInstance(
            @PathVariable Long processInstanceKey) {
        try {
            ProcessInstanceDTO processInstance = workflowService.getProcessInstance(processInstanceKey);
            return ResponseEntity.ok(ApiResponse.success(processInstance));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("PROCESS_QUERY_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get process instance: " + e.getMessage(), new ApiResponse.ErrorDetails("PROCESS_QUERY_ERROR", "Failed to get process instance: " + e.getMessage())));
        }
    }

    @DeleteMapping("/process-instances/{processInstanceKey}")
    @Operation(summary = "Cancel Process Instance", description = "Cancel a running process instance")
    public ResponseEntity<ApiResponse<Void>> cancelProcessInstance(@PathVariable Long processInstanceKey) {
        try {
            workflowService.cancelProcessInstance(processInstanceKey);
            return ResponseEntity.ok(ApiResponse.success(null, "Process instance cancelled successfully"));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("PROCESS_CANCEL_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to cancel process instance: " + e.getMessage(), new ApiResponse.ErrorDetails("PROCESS_CANCEL_ERROR", "Failed to cancel process instance: " + e.getMessage())));
        }
    }

    @PostMapping("/decisions/{decisionId}/evaluate")
    @Operation(summary = "Evaluate DMN Decision", description = "Evaluate a DMN decision table")
    public ResponseEntity<ApiResponse<DecisionResult>> evaluateDecision(
            @PathVariable String decisionId,
            @RequestBody(required = false) Map<String, Object> variables) {
        try {
            DecisionResult result = workflowService.evaluateDecision(decisionId, variables);
            return ResponseEntity.ok(ApiResponse.success(result, "Decision evaluated successfully"));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("DECISION_EVALUATION_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to evaluate decision: " + e.getMessage(), new ApiResponse.ErrorDetails("DECISION_EVALUATION_ERROR", "Failed to evaluate decision: " + e.getMessage())));
        }
    }

    @PostMapping("/tasks/{taskId}/assign")
    @Operation(summary = "Assign Task", description = "Assign a task to a user")
    public ResponseEntity<ApiResponse<Void>> assignTask(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("userId is required", new ApiResponse.ErrorDetails("VALIDATION_ERROR", "userId is required")));
            }
            workflowService.assignTask(taskId, userId);
            return ResponseEntity.ok(ApiResponse.success(null, "Task assigned successfully"));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("TASK_ASSIGN_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to assign task: " + e.getMessage(), new ApiResponse.ErrorDetails("TASK_ASSIGN_ERROR", "Failed to assign task: " + e.getMessage())));
        }
    }

    @PostMapping("/tasks/{taskId}/claim")
    @Operation(summary = "Claim Task", description = "Claim an unassigned task")
    public ResponseEntity<ApiResponse<Void>> claimTask(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("userId is required", new ApiResponse.ErrorDetails("VALIDATION_ERROR", "userId is required")));
            }
            workflowService.claimTask(taskId, userId);
            return ResponseEntity.ok(ApiResponse.success(null, "Task claimed successfully"));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("TASK_CLAIM_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to claim task: " + e.getMessage(), new ApiResponse.ErrorDetails("TASK_CLAIM_ERROR", "Failed to claim task: " + e.getMessage())));
        }
    }

    @PostMapping("/tasks/{taskId}/unclaim")
    @Operation(summary = "Unclaim Task", description = "Unclaim a task (make it available for others)")
    public ResponseEntity<ApiResponse<Void>> unclaimTask(@PathVariable Long taskId) {
        try {
            workflowService.unclaimTask(taskId);
            return ResponseEntity.ok(ApiResponse.success(null, "Task unclaimed successfully"));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("TASK_UNCLAIM_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to unclaim task: " + e.getMessage(), new ApiResponse.ErrorDetails("TASK_UNCLAIM_ERROR", "Failed to unclaim task: " + e.getMessage())));
        }
    }

    @PostMapping("/tasks/{taskId}/reassign")
    @Operation(summary = "Reassign Task", description = "Reassign a task to a different user")
    public ResponseEntity<ApiResponse<Void>> reassignTask(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("userId is required", new ApiResponse.ErrorDetails("VALIDATION_ERROR", "userId is required")));
            }
            workflowService.reassignTask(taskId, userId);
            return ResponseEntity.ok(ApiResponse.success(null, "Task reassigned successfully"));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("TASK_REASSIGN_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to reassign task: " + e.getMessage(), new ApiResponse.ErrorDetails("TASK_REASSIGN_ERROR", "Failed to reassign task: " + e.getMessage())));
        }
    }

    @PostMapping("/tasks/{taskId}/return")
    @Operation(summary = "Return Task", description = "Return a task to the previous step in the workflow")
    public ResponseEntity<ApiResponse<Void>> returnTask(
            @PathVariable Long taskId,
            @RequestBody(required = false) Map<String, String> request) {
        try {
            String reason = request != null ? request.get("reason") : null;
            workflowService.returnTask(taskId, reason);
            return ResponseEntity.ok(ApiResponse.success(null, "Task returned successfully"));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("TASK_RETURN_ERROR", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to return task: " + e.getMessage(), new ApiResponse.ErrorDetails("TASK_RETURN_ERROR", "Failed to return task: " + e.getMessage())));
        }
    }

    @GetMapping("/process-definitions/{processDefinitionKey}/bpmn")
    @Operation(summary = "Get BPMN XML", description = "Get BPMN XML for a process definition")
    public ResponseEntity<ApiResponse<String>> getBpmnXml(@PathVariable String processDefinitionKey) {
        try {
            String bpmnXml = workflowService.getBpmnXml(processDefinitionKey);
            return ResponseEntity.ok(ApiResponse.success(bpmnXml, "BPMN XML retrieved successfully"));
        } catch (WorkflowException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("BPMN_NOT_FOUND", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get BPMN XML: " + e.getMessage(), new ApiResponse.ErrorDetails("BPMN_RETRIEVAL_ERROR", "Failed to get BPMN XML: " + e.getMessage())));
        }
    }

    @GetMapping("/process-instances/{processInstanceKey}/activities")
    @Operation(summary = "Get Flow Node Instances", description = "Get all activity instances (completed, active, terminated) for a process instance")
    public ResponseEntity<ApiResponse<List<com.hhsa.workflow.dto.ActivityInstanceDTO>>> getFlowNodeInstances(
            @PathVariable Long processInstanceKey) {
        try {
            List<com.hhsa.workflow.dto.ActivityInstanceDTO> activities = workflowService.getFlowNodeInstances(processInstanceKey);
            // Always return success with empty list if no activities found (graceful degradation)
            return ResponseEntity.ok(ApiResponse.success(activities != null ? activities : new java.util.ArrayList<>(), "Flow node instances retrieved successfully"));
        } catch (WorkflowException e) {
            // Return empty list instead of error - allows frontend to still render diagram
            logger.warn("Failed to get flow node instances for process instance {}: {}", processInstanceKey, e.getMessage());
            return ResponseEntity.ok(ApiResponse.success(new java.util.ArrayList<>(), "No activity instances available"));
        } catch (Exception e) {
            // Return empty list instead of error - allows frontend to still render diagram
            logger.error("Failed to get flow node instances for process instance {}: {}", processInstanceKey, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.success(new java.util.ArrayList<>(), "No activity instances available"));
        }
    }

    @GetMapping("/process-instances/{processInstanceKey}/incidents")
    @Operation(summary = "Get Incidents", description = "Get all incidents (errors) for a process instance")
    public ResponseEntity<ApiResponse<List<com.hhsa.workflow.dto.IncidentDTO>>> getIncidents(
            @PathVariable Long processInstanceKey) {
        try {
            List<com.hhsa.workflow.dto.IncidentDTO> incidents = workflowService.getIncidents(processInstanceKey);
            // Always return success with empty list if no incidents found (graceful degradation)
            return ResponseEntity.ok(ApiResponse.success(incidents != null ? incidents : new java.util.ArrayList<>(), "Incidents retrieved successfully"));
        } catch (WorkflowException e) {
            // Return empty list instead of error - allows frontend to still render diagram
            logger.warn("Failed to get incidents for process instance {}: {}", processInstanceKey, e.getMessage());
            return ResponseEntity.ok(ApiResponse.success(new java.util.ArrayList<>(), "No incidents available"));
        } catch (Exception e) {
            // Return empty list instead of error - allows frontend to still render diagram
            logger.error("Failed to get incidents for process instance {}: {}", processInstanceKey, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.success(new java.util.ArrayList<>(), "No incidents available"));
        }
    }

    @GetMapping("/process-instances/{processInstanceKey}/task-history")
    @Operation(summary = "Get Task History", description = "Get detailed task history with creation, assignment, and completion timeline for a process instance")
    public ResponseEntity<ApiResponse<List<com.hhsa.workflow.dto.TaskHistoryDTO>>> getTaskHistory(
            @PathVariable Long processInstanceKey) {
        try {
            logger.debug("Getting task history for process instance: {}", processInstanceKey);
            List<com.hhsa.workflow.dto.TaskHistoryDTO> taskHistory = workflowService.getTaskHistory(processInstanceKey);
            // Always return success with empty list if no history found (graceful degradation)
            if (taskHistory == null) {
                logger.debug("Task history is null for process instance: {}, returning empty list", processInstanceKey);
                return ResponseEntity.ok(ApiResponse.success(new java.util.ArrayList<>(), "No task history available"));
            }
            logger.debug("Retrieved {} task history entries for process instance: {}", taskHistory.size(), processInstanceKey);
            return ResponseEntity.ok(ApiResponse.success(taskHistory, "Task history retrieved successfully"));
        } catch (WorkflowException e) {
            logger.warn("Failed to get task history for process instance {}: {}", processInstanceKey, e.getMessage());
            // Return empty list instead of error (graceful degradation)
            return ResponseEntity.ok(ApiResponse.success(new java.util.ArrayList<>(), "No task history available"));
        } catch (Exception e) {
            logger.error("Unexpected error getting task history for process instance {}: {}", processInstanceKey, e.getMessage(), e);
            // Return empty list instead of error (graceful degradation)
            return ResponseEntity.ok(ApiResponse.success(new java.util.ArrayList<>(), "No task history available"));
        }
    }
}

