package com.hhsa.workflow.service;

import com.hhsa.workflow.dto.ActivityInstanceDTO;
import com.hhsa.workflow.dto.IncidentDTO;
import com.hhsa.workflow.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for integrating with Camunda 8 Operate API.
 * Provides task queries and process instance information.
 */
@Service
public class OperateApiService {

    private static final Logger logger = LoggerFactory.getLogger(OperateApiService.class);

    private final WebClient operateClient;
    private final String operateBaseUrl;

    public OperateApiService(
            @Value("${camunda.operate.base-url:http://localhost:8081}") String operateBaseUrl) {
        this.operateBaseUrl = operateBaseUrl;
        this.operateClient = WebClient.builder()
            .baseUrl(operateBaseUrl)
            .build();
    }

    /**
     * Get tasks for a user from Operate API
     */
    public List<TaskDTO> getTasksForUser(String userId) {
        try {
            logger.debug("Getting tasks for user: {} from Operate API", userId);

            // Query Operate API for tasks assigned to user
            // Note: Operate API structure may vary, this is a generic implementation
            Mono<Map> response = operateClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1/tasks")
                    .queryParam("assignee", userId)
                    .queryParam("state", "CREATED")
                    .build())
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.warn("Could not query Operate API for user tasks: {}", userId);
                    return Mono.just(Map.of("items", new ArrayList<>()));
                });

            Map<String, Object> result = response.block();
            List<TaskDTO> tasks = new ArrayList<>();

            if (result != null && result.containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) result.get("items");
                for (Map<String, Object> item : items) {
                    TaskDTO task = mapToTaskDTO(item);
                    tasks.add(task);
                }
            }

            logger.info("Found {} tasks for user: {}", tasks.size(), userId);
            return tasks;

        } catch (Exception e) {
            logger.error("Failed to get tasks for user: {}", userId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get tasks for a process instance
     */
    public List<TaskDTO> getTasksByProcessInstance(Long processInstanceKey) {
        try {
            logger.debug("Getting tasks for process instance: {} from Operate API", processInstanceKey);

            // Try querying without state filter first to get all tasks
            Mono<Map> response = operateClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1/tasks")
                    .queryParam("processInstanceKey", processInstanceKey)
                    // Remove state filter to get all tasks (CREATED, COMPLETED, etc.)
                    .build())
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.warn("Could not query Operate API for process instance tasks: {}", processInstanceKey);
                    return Mono.just(Map.of("items", new ArrayList<>()));
                });

            Map<String, Object> result = response.block();
            List<TaskDTO> tasks = new ArrayList<>();

            if (result != null && result.containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) result.get("items");
                for (Map<String, Object> item : items) {
                    TaskDTO task = mapToTaskDTO(item);
                    tasks.add(task);
                }
            }

            return tasks;

        } catch (Exception e) {
            logger.error("Failed to get tasks for process instance: {}", processInstanceKey, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get all tasks (for testing - queries without assignee filter)
     */
    public List<TaskDTO> getAllTasks(Long processInstanceKey) {
        try {
            logger.debug("Getting all tasks for process instance: {} from Operate API", processInstanceKey);

            // Query without assignee filter to get all tasks
            Mono<Map> response = operateClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1/tasks")
                    .queryParam("processInstanceKey", processInstanceKey)
                    .build())
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.warn("Could not query Operate API for all tasks: {}", processInstanceKey);
                    return Mono.just(Map.of("items", new ArrayList<>()));
                });

            Map<String, Object> result = response.block();
            List<TaskDTO> tasks = new ArrayList<>();

            if (result != null && result.containsKey("items")) {
                Object itemsObj = result.get("items");
                if (itemsObj instanceof List) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) itemsObj;
                    for (Map<String, Object> item : items) {
                        try {
                            TaskDTO task = mapToTaskDTO(item);
                            tasks.add(task);
                        } catch (Exception e) {
                            logger.warn("Failed to map task: {}", e.getMessage());
                        }
                    }
                }
            }

            logger.info("Found {} tasks for process instance: {}", tasks.size(), processInstanceKey);
            return tasks;

        } catch (Exception e) {
            logger.error("Failed to get all tasks for process instance: {}", processInstanceKey, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get process instance details
     */
    public Map<String, Object> getProcessInstance(Long processInstanceKey) {
        try {
            logger.debug("Getting process instance: {} from Operate API", processInstanceKey);

            Mono<Map> response = operateClient.get()
                .uri("/v1/process-instances/{key}", processInstanceKey)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.warn("Could not query Operate API for process instance: {}", processInstanceKey);
                    return Mono.just(Map.of());
                });

            return response.block();

        } catch (Exception e) {
            logger.error("Failed to get process instance: {}", processInstanceKey, e);
            return Map.of();
        }
    }

    /**
     * Map Operate API response to TaskDTO
     */
    private TaskDTO mapToTaskDTO(Map<String, Object> item) {
        TaskDTO task = new TaskDTO();
        
        if (item.containsKey("key")) {
            task.setTaskKey(Long.valueOf(item.get("key").toString()));
        }
        if (item.containsKey("id")) {
            task.setTaskId(item.get("id").toString());
        }
        if (item.containsKey("name")) {
            task.setTaskType(item.get("name").toString());
        }
        if (item.containsKey("processInstanceKey")) {
            task.setProcessInstanceKey(Long.valueOf(item.get("processInstanceKey").toString()));
        }
        if (item.containsKey("processDefinitionId")) {
            task.setProcessDefinitionId(item.get("processDefinitionId").toString());
        }
        if (item.containsKey("assignee")) {
            task.setAssignee(item.get("assignee").toString());
        }
        if (item.containsKey("creationTime")) {
            // Parse timestamp
            String creationTime = item.get("creationTime").toString();
            // Convert to LocalDateTime (simplified)
            task.setCreationTime(LocalDateTime.now());
        }
        if (item.containsKey("state")) {
            task.setState(item.get("state").toString());
        }
        if (item.containsKey("variables")) {
            task.setVariables((Map<String, Object>) item.get("variables"));
        }

        return task;
    }

    /**
     * Assign a task to a user
     */
    public void assignTask(Long taskId, String userId) {
        try {
            logger.debug("Assigning task: {} to user: {} via Operate API", taskId, userId);

            Map<String, Object> requestBody = Map.of("assignee", userId);

            Mono<Map> response = operateClient.post()
                .uri("/v1/tasks/{taskId}/assign", taskId)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.error("Failed to assign task via Operate API: {}", taskId, ex);
                    throw new RuntimeException("Failed to assign task: " + ex.getMessage());
                });

            response.block();
            logger.info("Task assigned successfully: {} to user: {}", taskId, userId);

        } catch (Exception e) {
            logger.error("Failed to assign task: {}", taskId, e);
            throw new RuntimeException("Failed to assign task: " + e.getMessage(), e);
        }
    }

    /**
     * Claim an unassigned task
     */
    public void claimTask(Long taskId, String userId) {
        try {
            logger.debug("Claiming task: {} by user: {} via Operate API", taskId, userId);

            Map<String, Object> requestBody = Map.of("assignee", userId);

            Mono<Map> response = operateClient.post()
                .uri("/v1/tasks/{taskId}/claim", taskId)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.error("Failed to claim task via Operate API: {}", taskId, ex);
                    throw new RuntimeException("Failed to claim task: " + ex.getMessage());
                });

            response.block();
            logger.info("Task claimed successfully: {} by user: {}", taskId, userId);

        } catch (Exception e) {
            logger.error("Failed to claim task: {}", taskId, e);
            throw new RuntimeException("Failed to claim task: " + e.getMessage(), e);
        }
    }

    /**
     * Unclaim a task
     */
    public void unclaimTask(Long taskId) {
        try {
            logger.debug("Unclaiming task: {} via Operate API", taskId);

            Mono<Map> response = operateClient.post()
                .uri("/v1/tasks/{taskId}/unclaim", taskId)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.error("Failed to unclaim task via Operate API: {}", taskId, ex);
                    throw new RuntimeException("Failed to unclaim task: " + ex.getMessage());
                });

            response.block();
            logger.info("Task unclaimed successfully: {}", taskId);

        } catch (Exception e) {
            logger.error("Failed to unclaim task: {}", taskId, e);
            throw new RuntimeException("Failed to unclaim task: " + e.getMessage(), e);
        }
    }

    /**
     * Complete a task via Operate API
     */
    public void completeTask(Long taskId, Map<String, Object> variables) {
        try {
            logger.debug("Completing task: {} via Operate API", taskId);

            Map<String, Object> requestBody = new HashMap<>();
            if (variables != null && !variables.isEmpty()) {
                requestBody.put("variables", variables);
            }

            Mono<Map> response = operateClient.post()
                .uri("/v1/tasks/{taskId}/complete", taskId)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.error("Failed to complete task via Operate API: {}", taskId, ex);
                    throw new RuntimeException("Failed to complete task: " + ex.getMessage());
                });

            response.block();
            logger.info("Task completed successfully: {}", taskId);

        } catch (Exception e) {
            logger.error("Failed to complete task: {}", taskId, e);
            throw new RuntimeException("Failed to complete task: " + e.getMessage(), e);
        }
    }

    /**
     * Get task history for a process instance
     * Returns detailed timeline of task creation, assignment, and completion
     */
    public List<com.hhsa.workflow.dto.TaskHistoryDTO> getTaskHistory(Long processInstanceKey) {
        try {
            logger.debug("Getting task history for process instance: {} from Operate API", processInstanceKey);

            // Query for all tasks (including completed ones) for this process instance
            Mono<Map> response = operateClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1/tasks")
                    .queryParam("processInstanceKey", processInstanceKey)
                    // Don't filter by state to get all tasks (CREATED, COMPLETED, etc.)
                    .build())
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.warn("Could not query Operate API for task history: {} - {}", processInstanceKey, ex.getMessage());
                    return Mono.just(Map.of("items", new ArrayList<>()));
                });

            Map<String, Object> result = response.block();
            List<com.hhsa.workflow.dto.TaskHistoryDTO> taskHistory = new ArrayList<>();

            if (result != null && result.containsKey("items")) {
                Object itemsObj = result.get("items");
                if (itemsObj instanceof List) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) itemsObj;
                    for (Map<String, Object> item : items) {
                        try {
                            com.hhsa.workflow.dto.TaskHistoryDTO history = mapToTaskHistoryDTO(item);
                            taskHistory.add(history);
                        } catch (Exception e) {
                            logger.warn("Failed to map task history: {}", e.getMessage());
                        }
                    }
                }
            }

            // Sort by creation time (oldest first)
            taskHistory.sort((a, b) -> {
                if (a.getCreationTime() == null && b.getCreationTime() == null) return 0;
                if (a.getCreationTime() == null) return 1;
                if (b.getCreationTime() == null) return -1;
                return a.getCreationTime().compareTo(b.getCreationTime());
            });

            logger.debug("Found {} task history entries for process instance: {}", taskHistory.size(), processInstanceKey);
            return taskHistory;

        } catch (Exception e) {
            logger.error("Failed to get task history for process instance: {}", processInstanceKey, e);
            return new ArrayList<>();
        }
    }

    /**
     * Map Operate API task response to TaskHistoryDTO
     */
    private com.hhsa.workflow.dto.TaskHistoryDTO mapToTaskHistoryDTO(Map<String, Object> item) {
        com.hhsa.workflow.dto.TaskHistoryDTO history = new com.hhsa.workflow.dto.TaskHistoryDTO();

        if (item.containsKey("key")) {
            history.setTaskKey(Long.valueOf(item.get("key").toString()));
        }
        if (item.containsKey("id")) {
            history.setTaskId(item.get("id").toString());
        }
        if (item.containsKey("name")) {
            history.setTaskName(item.get("name").toString());
            history.setTaskType(item.get("name").toString());
        }
        if (item.containsKey("processInstanceKey")) {
            history.setProcessInstanceKey(Long.valueOf(item.get("processInstanceKey").toString()));
        }
        if (item.containsKey("processDefinitionId")) {
            history.setProcessDefinitionId(item.get("processDefinitionId").toString());
        }
        if (item.containsKey("creationTime")) {
            history.setCreationTime(parseTimestamp(item.get("creationTime").toString()));
        }
        if (item.containsKey("completionTime") || item.containsKey("endTime")) {
            Object completionTime = item.get("completionTime");
            if (completionTime == null) {
                completionTime = item.get("endTime");
            }
            if (completionTime != null) {
                history.setCompletionTime(parseTimestamp(completionTime.toString()));
            }
        }
        if (item.containsKey("assignee")) {
            String assignee = item.get("assignee").toString();
            history.setAssignee(assignee);
            history.setAssignedTo(assignee);
            // If assignee exists, assume assignment happened at creation time
            if (history.getCreationTime() != null) {
                history.setAssignmentTime(history.getCreationTime());
            }
        }
        if (item.containsKey("state")) {
            history.setState(item.get("state").toString());
        }
        if (item.containsKey("dueDate")) {
            history.setDueDate(parseTimestamp(item.get("dueDate").toString()));
        }
        if (item.containsKey("candidateUser")) {
            history.setCandidateUser(item.get("candidateUser").toString());
        }
        if (item.containsKey("candidateGroup")) {
            history.setCandidateGroup(item.get("candidateGroup").toString());
        }
        // For completed tasks, completedBy is typically the assignee
        if ("COMPLETED".equals(history.getState()) && history.getAssignee() != null) {
            history.setCompletedBy(history.getAssignee());
        }

        return history;
    }

    /**
     * Get flow node instances (activities) for a process instance
     * This includes all activities (completed, active, terminated) for visualization
     */
    public List<ActivityInstanceDTO> getFlowNodeInstances(Long processInstanceKey) {
        try {
            logger.debug("Getting flow node instances for process instance: {} from Operate API", processInstanceKey);

            Mono<Map> response = operateClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1/flow-node-instances")
                    .queryParam("processInstanceKey", processInstanceKey)
                    .build())
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    // Handle both connection errors and HTTP error status codes
                    if (ex instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
                        org.springframework.web.reactive.function.client.WebClientResponseException httpEx = 
                            (org.springframework.web.reactive.function.client.WebClientResponseException) ex;
                        logger.warn("Operate API returned error status: {} for flow node instances query. Process instance may not exist or Operate API unavailable.", httpEx.getStatusCode());
                    } else {
                        logger.warn("Could not query Operate API for flow node instances: {} - {}", processInstanceKey, ex.getMessage());
                    }
                    return Mono.just(Map.of("items", new ArrayList<>()));
                });

            Map<String, Object> result = response.block();
            List<ActivityInstanceDTO> activities = new ArrayList<>();

            if (result != null && result.containsKey("items")) {
                Object itemsObj = result.get("items");
                if (itemsObj instanceof List) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) itemsObj;
                    for (Map<String, Object> item : items) {
                        try {
                            ActivityInstanceDTO activity = mapToActivityInstanceDTO(item);
                            activities.add(activity);
                        } catch (Exception e) {
                            logger.warn("Failed to map activity instance: {}", e.getMessage());
                        }
                    }
                }
            }

            logger.debug("Found {} flow node instances for process instance: {}", activities.size(), processInstanceKey);
            return activities;

        } catch (Exception e) {
            logger.error("Failed to get flow node instances for process instance: {}", processInstanceKey, e);
            // Return empty list instead of throwing - allows frontend to still render diagram
            return new ArrayList<>();
        }
    }

    /**
     * Get incidents (errors) for a process instance
     */
    public List<IncidentDTO> getIncidents(Long processInstanceKey) {
        try {
            logger.debug("Getting incidents for process instance: {} from Operate API", processInstanceKey);

            Mono<Map> response = operateClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1/incidents")
                    .queryParam("processInstanceKey", processInstanceKey)
                    .queryParam("state", "OPEN")
                    .build())
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    // Handle both connection errors and HTTP error status codes
                    if (ex instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
                        org.springframework.web.reactive.function.client.WebClientResponseException httpEx = 
                            (org.springframework.web.reactive.function.client.WebClientResponseException) ex;
                        logger.warn("Operate API returned error status: {} for incidents query. Process instance may not exist or Operate API unavailable.", httpEx.getStatusCode());
                    } else {
                        logger.warn("Could not query Operate API for incidents: {} - {}", processInstanceKey, ex.getMessage());
                    }
                    return Mono.just(Map.of("items", new ArrayList<>()));
                });

            Map<String, Object> result = response.block();
            List<IncidentDTO> incidents = new ArrayList<>();

            if (result != null && result.containsKey("items")) {
                Object itemsObj = result.get("items");
                if (itemsObj instanceof List) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) itemsObj;
                    for (Map<String, Object> item : items) {
                        try {
                            IncidentDTO incident = mapToIncidentDTO(item);
                            incidents.add(incident);
                        } catch (Exception e) {
                            logger.warn("Failed to map incident: {}", e.getMessage());
                        }
                    }
                }
            }

            logger.debug("Found {} incidents for process instance: {}", incidents.size(), processInstanceKey);
            return incidents;

        } catch (Exception e) {
            logger.error("Failed to get incidents for process instance: {}", processInstanceKey, e);
            // Return empty list instead of throwing - allows frontend to still render diagram
            return new ArrayList<>();
        }
    }

    /**
     * Map Operate API response to ActivityInstanceDTO
     */
    private ActivityInstanceDTO mapToActivityInstanceDTO(Map<String, Object> item) {
        ActivityInstanceDTO activity = new ActivityInstanceDTO();

        if (item.containsKey("key")) {
            activity.setActivityInstanceKey(Long.valueOf(item.get("key").toString()));
        }
        if (item.containsKey("flowNodeId")) {
            activity.setActivityId(item.get("flowNodeId").toString());
        }
        if (item.containsKey("flowNodeName")) {
            activity.setActivityName(item.get("flowNodeName").toString());
        }
        if (item.containsKey("type")) {
            activity.setActivityType(item.get("type").toString());
        }
        if (item.containsKey("state")) {
            activity.setState(item.get("state").toString());
        }
        if (item.containsKey("startDate")) {
            activity.setStartTime(parseTimestamp(item.get("startDate").toString()));
        }
        if (item.containsKey("endDate")) {
            activity.setEndTime(parseTimestamp(item.get("endDate").toString()));
        }
        if (item.containsKey("assignee")) {
            activity.setAssignee(item.get("assignee").toString());
        }

        return activity;
    }

    /**
     * Map Operate API response to IncidentDTO
     */
    private IncidentDTO mapToIncidentDTO(Map<String, Object> item) {
        IncidentDTO incident = new IncidentDTO();

        if (item.containsKey("key")) {
            incident.setIncidentKey(Long.valueOf(item.get("key").toString()));
        }
        if (item.containsKey("errorType")) {
            incident.setIncidentType(item.get("errorType").toString());
        }
        if (item.containsKey("errorMessage")) {
            incident.setErrorMessage(item.get("errorMessage").toString());
        }
        if (item.containsKey("creationTime")) {
            incident.setCreationTime(parseTimestamp(item.get("creationTime").toString()));
        }
        if (item.containsKey("resolutionTime")) {
            incident.setResolutionTime(parseTimestamp(item.get("resolutionTime").toString()));
        }
        if (item.containsKey("state")) {
            incident.setState(item.get("state").toString());
        }

        return incident;
    }

    /**
     * Parse timestamp from Operate API (ISO 8601 format)
     */
    private LocalDateTime parseTimestamp(String timestamp) {
        try {
            if (timestamp == null || timestamp.isEmpty()) {
                return null;
            }
            // Try parsing ISO 8601 format
            java.time.ZonedDateTime zonedDateTime = java.time.ZonedDateTime.parse(timestamp, java.time.format.DateTimeFormatter.ISO_DATE_TIME);
            return zonedDateTime.toLocalDateTime();
        } catch (Exception e) {
            logger.warn("Failed to parse timestamp: {}", timestamp, e);
            return null;
        }
    }
}

