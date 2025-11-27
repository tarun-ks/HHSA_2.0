package com.hhsa.contract.controller;

import com.hhsa.common.core.dto.ApiResponse;
import com.hhsa.contract.dto.TaskCompleteRequest;
import com.hhsa.contract.dto.WorkflowTaskDTO;
import com.hhsa.contract.service.WorkflowIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Workflow controller for contract-related workflow operations.
 * Provides endpoints for workflow task management.
 */
@RestController
@RequestMapping("/api/v1/contracts/workflows")
@Tag(name = "Contract Workflows", description = "Contract workflow task management endpoints")
public class WorkflowController {

    private final WorkflowIntegrationService workflowIntegrationService;

    public WorkflowController(WorkflowIntegrationService workflowIntegrationService) {
        this.workflowIntegrationService = workflowIntegrationService;
    }

    @GetMapping("/contracts/{contractId}/tasks")
    @Operation(summary = "Get Contract Tasks", description = "Get workflow tasks for a contract")
    public ResponseEntity<ApiResponse<List<WorkflowTaskDTO>>> getContractTasks(
            @PathVariable Long contractId) {
        try {
            List<Map<String, Object>> tasks = workflowIntegrationService.getContractTasks(contractId);
            // Convert to DTOs (simplified - in production would map properly)
            List<WorkflowTaskDTO> taskDTOs = tasks.stream()
                .map(task -> {
                    WorkflowTaskDTO dto = new WorkflowTaskDTO();
                    // Map task data to DTO
                    return dto;
                })
                .toList();

            return ResponseEntity.ok(ApiResponse.success(taskDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get tasks: " + e.getMessage(), new ApiResponse.ErrorDetails("QUERY_ERROR", "Failed to get tasks: " + e.getMessage())));
        }
    }

    @PostMapping("/tasks/complete")
    @Operation(summary = "Complete Workflow Task", description = "Complete a workflow task")
    public ResponseEntity<ApiResponse<Void>> completeTask(
            @Valid @RequestBody TaskCompleteRequest request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "system") String userId) {
        try {
            workflowIntegrationService.completeWorkflowTask(
                request.getTaskId(),
                request.getVariables(),
                userId
            );

            // Update contract status based on workflow outcome
            // In production, this would be handled by workflow callbacks/events
            if (request.getApproved() != null && request.getApproved()) {
                // Handle approval - update contract status accordingly
            }

            return ResponseEntity.ok(ApiResponse.success(null, "Task completed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("TASK_COMPLETE_ERROR", e.getMessage())));
        }
    }
}

