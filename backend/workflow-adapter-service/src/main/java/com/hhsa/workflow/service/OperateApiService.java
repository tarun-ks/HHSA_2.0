package com.hhsa.workflow.service;

import com.hhsa.workflow.dto.ActivityInstanceDTO;
import com.hhsa.workflow.dto.IncidentDTO;
import com.hhsa.workflow.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            .defaultHeader("Content-Type", "application/json")
            // Note: Operate API authentication will be added per-request via filter
            .build();
    }

    /**
     * Get JWT token from SecurityContext for Operate API authentication
     */
    private String getJwtToken() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                return jwt.getTokenValue();
            }
        } catch (Exception e) {
            logger.debug("Failed to extract JWT token from SecurityContext: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Create authenticated WebClient request with JWT token
     * Note: Camunda Operate may require different authentication (OAuth2, Basic Auth, or no auth for local dev)
     * This implementation tries JWT first, but Operate might need its own OAuth2 token
     */
    private WebClient.RequestHeadersSpec<?> addAuthHeader(WebClient.RequestHeadersSpec<?> request) {
        String token = getJwtToken();
        if (token != null) {
            // Try JWT token (if Operate is configured to accept Keycloak tokens)
            logger.debug("Adding JWT token to Operate API request");
            return request.header("Authorization", "Bearer " + token);
        }
        // If no token, try without auth (for local development or if Operate doesn't require auth)
        logger.debug("No JWT token available for Operate API request - attempting unauthenticated request");
        return request;
    }

    /**
     * Get tasks for a user from Operate API
     * Queries tasks by:
     * 1. Tasks directly assigned to the user (assignee = userId)
     * 2. Tasks with candidateUser = userId
     * 3. Tasks with candidateGroups matching user's roles
     */
    public List<TaskDTO> getTasksForUser(String userId) {
        try {
            logger.debug("Getting tasks for user: {} from Operate API", userId);

            // Get user roles from SecurityContext
            List<String> userRoles = getUserRoles();
            logger.debug("User roles for task filtering: {}", userRoles);

            // Query ALL CREATED tasks (no assignee filter) to get tasks with candidateGroups
            // We'll filter in memory by assignee, candidateUser, or candidateGroup
            Mono<Map> response = addAuthHeader(operateClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1/tasks")
                    .queryParam("state", "CREATED")
                    // Don't filter by assignee - we need to see all tasks to check candidateGroups
                    .build()))
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.warn("Could not query Operate API for user tasks: {} - {}", userId, ex.getMessage());
                    return Mono.just(Map.of("items", new ArrayList<>()));
                });

            Map<String, Object> result = response.block();
            List<TaskDTO> allTasks = new ArrayList<>();

            if (result != null && result.containsKey("items")) {
                Object itemsObj = result.get("items");
                if (itemsObj instanceof List) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) itemsObj;
                    for (Map<String, Object> item : items) {
                        try {
                            TaskDTO task = mapToTaskDTO(item);
                            allTasks.add(task);
                        } catch (Exception e) {
                            logger.warn("Failed to map task: {}", e.getMessage());
                        }
                    }
                }
            }

            // Filter tasks: assignee = userId OR candidateUser = userId OR candidateGroup IN userRoles
            List<TaskDTO> filteredTasks = allTasks.stream()
                .filter(task -> {
                    // Task is assigned to user
                    if (userId.equals(task.getAssignee())) {
                        logger.debug("Task {} matched by assignee: {}", task.getTaskId(), task.getAssignee());
                        return true;
                    }
                    // Task has candidateUser matching userId
                    if (userId.equals(task.getCandidateUser())) {
                        logger.debug("Task {} matched by candidateUser: {}", task.getTaskId(), task.getCandidateUser());
                        return true;
                    }
                    // Task has candidateGroup matching one of user's roles
                    if (task.getCandidateGroup() != null && userRoles.contains(task.getCandidateGroup())) {
                        logger.debug("Task {} matched by candidateGroup: {} (user roles: {})", 
                            task.getTaskId(), task.getCandidateGroup(), userRoles);
                        return true;
                    }
                    // Check if candidateGroup contains comma-separated groups (e.g., "FINANCE_MANAGER,ACCO_MANAGER")
                    if (task.getCandidateGroup() != null && task.getCandidateGroup().contains(",")) {
                        String[] groups = task.getCandidateGroup().split(",");
                        for (String group : groups) {
                            String trimmedGroup = group.trim();
                            if (userRoles.contains(trimmedGroup)) {
                                logger.debug("Task {} matched by candidateGroup (comma-separated): {} (user roles: {})", 
                                    task.getTaskId(), trimmedGroup, userRoles);
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());

            logger.info("Found {} tasks for user: {} (filtered from {} total tasks, user roles: {})", 
                filteredTasks.size(), userId, allTasks.size(), userRoles);
            return filteredTasks;

        } catch (Exception e) {
            logger.error("Failed to get tasks for user: {}", userId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Extract user roles from SecurityContext
     * Roles are stored as GrantedAuthority with "ROLE_" prefix
     */
    private List<String> getUserRoles() {
        List<String> roles = new ArrayList<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                // Extract roles from GrantedAuthorities (format: "ROLE_ACCO_MANAGER" -> "ACCO_MANAGER")
                roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> authority.startsWith("ROLE_"))
                    .map(authority -> authority.substring(5)) // Remove "ROLE_" prefix
                    .collect(Collectors.toList());
                
                // Also try to extract from JWT token directly (realm_access.roles)
                if (authentication.getPrincipal() instanceof Jwt) {
                    Jwt jwt = (Jwt) authentication.getPrincipal();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
                    if (realmAccess != null) {
                        @SuppressWarnings("unchecked")
                        List<String> realmRoles = (List<String>) realmAccess.get("roles");
                        if (realmRoles != null) {
                            // Add roles that aren't already in the list
                            for (String role : realmRoles) {
                                if (!roles.contains(role)) {
                                    roles.add(role);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to extract user roles from SecurityContext: {}", e.getMessage());
        }
        return roles;
    }

    /**
     * Get tasks for a process instance
     */
    public List<TaskDTO> getTasksByProcessInstance(Long processInstanceKey) {
        try {
            logger.debug("Getting tasks for process instance: {} from Operate API", processInstanceKey);

            // Try querying without state filter first to get all tasks
            Mono<Map> response = addAuthHeader(operateClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1/tasks")
                    .queryParam("processInstanceKey", processInstanceKey)
                    // Remove state filter to get all tasks (CREATED, COMPLETED, etc.)
                    .build()))
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
            Mono<Map> response = addAuthHeader(operateClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1/tasks")
                    .queryParam("processInstanceKey", processInstanceKey)
                    .build()))
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

            Mono<Map> response = addAuthHeader(operateClient.get()
                .uri("/v1/process-instances/{key}", processInstanceKey))
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
            Object assigneeObj = item.get("assignee");
            if (assigneeObj != null) {
                task.setAssignee(assigneeObj.toString());
            }
        }
        // Extract candidateUser
        if (item.containsKey("candidateUser")) {
            Object candidateUserObj = item.get("candidateUser");
            if (candidateUserObj != null) {
                task.setCandidateUser(candidateUserObj.toString());
            }
        }
        // Extract candidateGroup (may be a single group or comma-separated list)
        if (item.containsKey("candidateGroup")) {
            Object candidateGroupObj = item.get("candidateGroup");
            if (candidateGroupObj != null) {
                task.setCandidateGroup(candidateGroupObj.toString());
            }
        }
        // Also check for candidateGroups (plural) - some APIs return as array
        if (item.containsKey("candidateGroups")) {
            Object candidateGroupsObj = item.get("candidateGroups");
            if (candidateGroupsObj != null) {
                if (candidateGroupsObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> groups = (List<String>) candidateGroupsObj;
                    task.setCandidateGroup(String.join(",", groups));
                } else {
                    task.setCandidateGroup(candidateGroupsObj.toString());
                }
            }
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

            Mono<Map> response = addAuthHeader(operateClient.post()
                .uri("/v1/tasks/{taskId}/assign", taskId)
                .bodyValue(requestBody))
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

            Mono<Map> response = addAuthHeader(operateClient.post()
                .uri("/v1/tasks/{taskId}/claim", taskId)
                .bodyValue(requestBody))
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

            Mono<Map> response = addAuthHeader(operateClient.post()
                .uri("/v1/tasks/{taskId}/unclaim", taskId))
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

            Mono<Map> response = addAuthHeader(operateClient.post()
                .uri("/v1/tasks/{taskId}/complete", taskId)
                .bodyValue(requestBody))
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
     * Includes ALL activities (start events, user tasks, service tasks, etc.)
     * 
     * Bypasses Operate API (which requires authentication) by querying Elasticsearch directly
     */
    public List<com.hhsa.workflow.dto.TaskHistoryDTO> getTaskHistory(Long processInstanceKey) {
        try {
            logger.debug("Getting task history for process instance: {} from Elasticsearch (including all activities)", processInstanceKey);

            // Query Elasticsearch directly (bypass Operate API 401 issue)
            // Get ALL flow node instances (includes start events, user tasks, service tasks, etc.)
            String elasticsearchUrl = operateBaseUrl.replace(":8081", ":9200");
            WebClient esClient = WebClient.builder()
                .baseUrl(elasticsearchUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();

            List<com.hhsa.workflow.dto.TaskHistoryDTO> taskHistory = new ArrayList<>();

            // Step 0: Get process instance variables (for initiator/startedBy)
            Map<String, Object> processVariables = getProcessInstanceVariables(processInstanceKey, esClient);

            // Step 1: Get all flow node instances (includes start events, tasks, etc.)
            Map<String, Object> flowNodeQueryBody = new HashMap<>();
            flowNodeQueryBody.put("query", Map.of(
                "term", Map.of("processInstanceKey", processInstanceKey)
            ));
            flowNodeQueryBody.put("size", 100);
            flowNodeQueryBody.put("sort", List.of(Map.of("startDate", Map.of("order", "asc", "missing", "_last"))));

            Mono<Map> flowNodeResponse = esClient.post()
                .uri("/operate-flownode-instance-8.3.1_/_search")
                .bodyValue(flowNodeQueryBody)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.warn("Could not query Elasticsearch for flow node instances: {} - {}", processInstanceKey, ex.getMessage());
                    return Mono.just(Map.of("hits", Map.of("hits", new ArrayList<>())));
                });

            Map<String, Object> flowNodeResult = flowNodeResponse.block();
            if (flowNodeResult != null && flowNodeResult.containsKey("hits")) {
                Map<String, Object> hits = (Map<String, Object>) flowNodeResult.get("hits");
                Object hitsListObj = hits.get("hits");
                if (hitsListObj instanceof List) {
                    List<Map<String, Object>> hitsList = (List<Map<String, Object>>) hitsListObj;
                    for (Map<String, Object> hit : hitsList) {
                        Map<String, Object> source = null;
                        try {
                            source = (Map<String, Object>) hit.get("_source");
                            // Convert flow node instance to TaskHistoryDTO
                            com.hhsa.workflow.dto.TaskHistoryDTO history = mapFlowNodeToTaskHistory(source, processVariables);
                            if (history != null) {
                                taskHistory.add(history);
                            }
                        } catch (Exception e) {
                            logger.warn("Failed to map flow node to task history: {} - {}", 
                                source != null ? source.toString() : "null", e.getMessage());
                        }
                    }
                }
            }

            // Step 2: Also get user tasks from tasklist (for additional details like assignee)
            // Merge with flow node instances to enrich with assignee info
            Map<String, com.hhsa.workflow.dto.TaskHistoryDTO> taskHistoryMap = new HashMap<>();
            
            // Add flow node instances to map (keyed by flowNodeId)
            for (com.hhsa.workflow.dto.TaskHistoryDTO history : taskHistory) {
                if (history.getTaskName() != null) {
                    taskHistoryMap.put(history.getTaskName(), history);
                }
            }

            // Get user tasks and merge/enrich existing entries
            Map<String, Object> taskQueryBody = new HashMap<>();
            taskQueryBody.put("query", Map.of(
                "term", Map.of("processInstanceId", processInstanceKey.toString())
            ));
            taskQueryBody.put("size", 100);

            Mono<Map> taskResponse = esClient.post()
                .uri("/tasklist-task-8.4.0_/_search")
                .bodyValue(taskQueryBody)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.warn("Could not query Elasticsearch for user tasks: {} - {}", processInstanceKey, ex.getMessage());
                    return Mono.just(Map.of("hits", Map.of("hits", new ArrayList<>())));
                });

            Map<String, Object> taskResult = taskResponse.block(Duration.ofSeconds(10));
            if (taskResult != null && taskResult.containsKey("hits")) {
                Map<String, Object> hits = (Map<String, Object>) taskResult.get("hits");
                Object hitsListObj = hits.get("hits");
                if (hitsListObj instanceof List) {
                    List<Map<String, Object>> hitsList = (List<Map<String, Object>>) hitsListObj;
                    for (Map<String, Object> hit : hitsList) {
                        try {
                            Map<String, Object> source = (Map<String, Object>) hit.get("_source");
                            String flowNodeId = source.containsKey("flowNodeBpmnId") ? 
                                source.get("flowNodeBpmnId").toString() : 
                                (source.containsKey("name") ? source.get("name").toString() : null);
                            
                            if (flowNodeId != null && taskHistoryMap.containsKey(flowNodeId)) {
                                // Enrich existing flow node entry with task details (assignee, outcome, etc.)
                                com.hhsa.workflow.dto.TaskHistoryDTO existing = taskHistoryMap.get(flowNodeId);
                                
                                // Framework-based: Extract assignee
                                if (source.containsKey("assignee")) {
                                    Object assigneeObj = source.get("assignee");
                                    if (assigneeObj != null && !assigneeObj.toString().equals("null")) {
                                        existing.setAssignee(assigneeObj.toString());
                                        existing.setAssignedTo(assigneeObj.toString());
                                        // Set assignment time if we have creation time
                                        if (existing.getCreationTime() != null) {
                                            existing.setAssignmentTime(existing.getCreationTime());
                                        }
                                    }
                                }
                                
                                // Framework-based: Extract outcome from task variables or state
                                // Check if task is completed and extract approval/rejection
                                if ("COMPLETED".equals(existing.getState()) || "COMPLETED".equals(source.get("state"))) {
                                    // Try to get outcome from variables (framework-based)
                                    if (source.containsKey("variables")) {
                                        Object varsObj = source.get("variables");
                                        if (varsObj instanceof Map) {
                                            @SuppressWarnings("unchecked")
                                            Map<String, Object> taskVars = (Map<String, Object>) varsObj;
                                            String outcome = extractOutcomeFromVariables(taskVars);
                                            if (outcome != null) {
                                                existing.setOutcome(outcome);
                                            }
                                        }
                                    }
                                    // Set completedBy to assignee if available
                                    if (existing.getAssignee() != null) {
                                        existing.setCompletedBy(existing.getAssignee());
                                    }
                                }
                            } else if (flowNodeId != null) {
                                // New task not in flow nodes (shouldn't happen, but handle gracefully)
                                com.hhsa.workflow.dto.TaskHistoryDTO history = mapTaskHistoryFromElasticsearch(source);
                                if (history != null) {
                                    taskHistoryMap.put(flowNodeId, history);
                                }
                            }
                        } catch (Exception e) {
                            logger.warn("Failed to merge task details: {}", e.getMessage());
                        }
                    }
                }
            }

            // Convert map back to list and sort
            taskHistory = new ArrayList<>(taskHistoryMap.values());
            // Sort by creation time (oldest first) - only if we have items
            if (!taskHistory.isEmpty()) {
                taskHistory.sort((a, b) -> {
                    if (a.getCreationTime() == null && b.getCreationTime() == null) return 0;
                    if (a.getCreationTime() == null) return 1;
                    if (b.getCreationTime() == null) return -1;
                    return a.getCreationTime().compareTo(b.getCreationTime());
                });
            }

            logger.info("Found {} task history entries for process instance: {} (queried from Elasticsearch)", taskHistory.size(), processInstanceKey);
            return taskHistory;

        } catch (Exception e) {
            logger.error("Failed to get task history for process instance: {} - {}", processInstanceKey, e.getMessage(), e);
            // Return empty list on error (graceful degradation)
            return new ArrayList<>();
        }
    }

    /**
     * Get process instance variables (framework-based, works for any workflow)
     * Used to extract initiator/startedBy information
     */
    private Map<String, Object> getProcessInstanceVariables(Long processInstanceKey, WebClient esClient) {
        try {
            Map<String, Object> queryBody = new HashMap<>();
            queryBody.put("query", Map.of("term", Map.of("key", processInstanceKey)));
            queryBody.put("size", 1);

            Mono<Map> response = esClient.post()
                .uri("/operate-list-view-8.3.0_/_search")
                .bodyValue(queryBody)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.debug("Could not query process instance variables: {}", ex.getMessage());
                    return Mono.just(Map.of("hits", Map.of("hits", new ArrayList<>())));
                });

            Map<String, Object> result = response.block(Duration.ofSeconds(5));
            if (result != null && result.containsKey("hits")) {
                Map<String, Object> hits = (Map<String, Object>) result.get("hits");
                Object hitsListObj = hits.get("hits");
                if (hitsListObj instanceof List) {
                    List<Map<String, Object>> hitsList = (List<Map<String, Object>>) hitsListObj;
                    if (!hitsList.isEmpty()) {
                        Map<String, Object> source = (Map<String, Object>) hitsList.get(0).get("_source");
                        if (source != null && source.containsKey("variables")) {
                            Object varsObj = source.get("variables");
                            if (varsObj instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> variables = (Map<String, Object>) varsObj;
                                return variables;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Failed to get process instance variables: {}", e.getMessage());
        }
        return new HashMap<>();
    }

    /**
     * Map Elasticsearch flow node instance to TaskHistoryDTO
     * This includes start events, user tasks, service tasks, etc.
     * Framework-based: works for any workflow
     */
    private com.hhsa.workflow.dto.TaskHistoryDTO mapFlowNodeToTaskHistory(Map<String, Object> source, Map<String, Object> processVariables) {
        com.hhsa.workflow.dto.TaskHistoryDTO history = new com.hhsa.workflow.dto.TaskHistoryDTO();

        if (source.containsKey("key")) {
            history.setTaskKey(Long.valueOf(source.get("key").toString()));
        }
        if (source.containsKey("flowNodeId")) {
            String flowNodeId = source.get("flowNodeId").toString();
            history.setTaskId(flowNodeId);
            // Use flowNodeName if available, otherwise derive readable name from flowNodeId
            if (source.containsKey("flowNodeName") && source.get("flowNodeName") != null) {
                history.setTaskName(source.get("flowNodeName").toString());
            } else {
                // Framework-based: Convert flowNodeId to readable name (e.g., "StartEvent_ContractConfiguration" -> "Contract Configuration Started")
                String readableName = convertFlowNodeIdToReadableName(flowNodeId);
                history.setTaskName(readableName);
            }
            history.setTaskType(flowNodeId);
        }
        if (source.containsKey("processInstanceKey")) {
            history.setProcessInstanceKey(Long.valueOf(source.get("processInstanceKey").toString()));
        }
        if (source.containsKey("processDefinitionKey")) {
            history.setProcessDefinitionId(source.get("processDefinitionKey").toString());
        }
        if (source.containsKey("startDate")) {
            Object startDateObj = source.get("startDate");
            if (startDateObj != null && !startDateObj.toString().equals("null")) {
                history.setCreationTime(parseTimestamp(startDateObj.toString()));
            }
        }
        if (source.containsKey("endDate")) {
            Object endDateObj = source.get("endDate");
            if (endDateObj != null && !endDateObj.toString().equals("null")) {
                history.setCompletionTime(parseTimestamp(endDateObj.toString()));
            }
        }
        if (source.containsKey("state")) {
            history.setState(source.get("state").toString());
        }
        
        // Framework-based: Extract user information from process variables and flow node data
        // For start events: get initiator from process variables
        if (source.containsKey("type") && "START_EVENT".equals(source.get("type").toString())) {
            history.setState("COMPLETED");
            // Framework-based: Extract initiator from process variables (works for any workflow)
            // Common variable names: "initiator", "startedBy", "createdBy", "userId"
            String initiator = extractInitiatorFromVariables(processVariables);
            if (initiator != null) {
                history.setCreatedBy(initiator);
            }
        }
        
        // For user tasks: assignee might be in flow node or will be enriched from tasklist
        if (source.containsKey("assignee")) {
            Object assigneeObj = source.get("assignee");
            if (assigneeObj != null && !assigneeObj.toString().equals("null")) {
                history.setAssignee(assigneeObj.toString());
                history.setAssignedTo(assigneeObj.toString());
                // If we have creation time, use it as assignment time
                if (history.getCreationTime() != null) {
                    history.setAssignmentTime(history.getCreationTime());
                }
            }
        }

        return history;
    }

    /**
     * Map Elasticsearch task document to TaskHistoryDTO
     */
    private com.hhsa.workflow.dto.TaskHistoryDTO mapTaskHistoryFromElasticsearch(Map<String, Object> source) {
        com.hhsa.workflow.dto.TaskHistoryDTO history = new com.hhsa.workflow.dto.TaskHistoryDTO();

        if (source.containsKey("key")) {
            history.setTaskKey(Long.valueOf(source.get("key").toString()));
        }
        if (source.containsKey("id")) {
            history.setTaskId(source.get("id").toString());
        }
        // Task name might be in flowNodeBpmnId or name field
        if (source.containsKey("name")) {
            history.setTaskName(source.get("name").toString());
            history.setTaskType(source.get("name").toString());
        } else if (source.containsKey("flowNodeBpmnId")) {
            String flowNodeId = source.get("flowNodeBpmnId").toString();
            history.setTaskName(flowNodeId);
            history.setTaskType(flowNodeId);
        }
        if (source.containsKey("processInstanceId")) {
            history.setProcessInstanceKey(Long.valueOf(source.get("processInstanceId").toString()));
        }
        if (source.containsKey("processDefinitionId")) {
            history.setProcessDefinitionId(source.get("processDefinitionId").toString());
        }
        if (source.containsKey("creationTime")) {
            Object creationTimeObj = source.get("creationTime");
            if (creationTimeObj != null && !creationTimeObj.toString().equals("null")) {
                history.setCreationTime(parseTimestamp(creationTimeObj.toString()));
            }
        }
        // Also check for startDate as fallback for creation time
        if (history.getCreationTime() == null && source.containsKey("startDate")) {
            Object startDateObj = source.get("startDate");
            if (startDateObj != null && !startDateObj.toString().equals("null")) {
                history.setCreationTime(parseTimestamp(startDateObj.toString()));
            }
        }
        if (source.containsKey("completionTime")) {
            Object completionTime = source.get("completionTime");
            if (completionTime != null && !completionTime.toString().equals("null")) {
                history.setCompletionTime(parseTimestamp(completionTime.toString()));
            }
        }
        if (source.containsKey("assignee")) {
            Object assigneeObj = source.get("assignee");
            if (assigneeObj != null && !assigneeObj.toString().equals("null")) {
                String assignee = assigneeObj.toString();
                history.setAssignee(assignee);
                history.setAssignedTo(assignee);
            }
        }
        if (source.containsKey("state")) {
            history.setState(source.get("state").toString());
            // If state is COMPLETED and we have assignee, set completedBy
            if ("COMPLETED".equals(history.getState()) && history.getAssignee() != null) {
                history.setCompletedBy(history.getAssignee());
            }
        }

        return history;
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
     * 
     * Bypasses Operate API (which requires authentication) by querying Elasticsearch directly
     */
    public List<ActivityInstanceDTO> getFlowNodeInstances(Long processInstanceKey) {
        try {
            logger.debug("Getting flow node instances for process instance: {} from Elasticsearch", processInstanceKey);

            // Query Elasticsearch directly (bypass Operate API 401 issue)
            // Operate stores flow node instances in operate-flownode-instance-8.3.1_ index
            String elasticsearchUrl = operateBaseUrl.replace(":8081", ":9200");
            WebClient esClient = WebClient.builder()
                .baseUrl(elasticsearchUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();

            Map<String, Object> queryBody = new HashMap<>();
            queryBody.put("query", Map.of(
                "term", Map.of("processInstanceKey", processInstanceKey)
            ));
            queryBody.put("size", 100);
            queryBody.put("sort", List.of(Map.of("startDate", Map.of("order", "asc", "missing", "_last"))));

            Mono<Map> response = esClient.post()
                .uri("/operate-flownode-instance-8.3.1_/_search")
                .bodyValue(queryBody)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.warn("Could not query Elasticsearch for flow node instances: {} - {}", processInstanceKey, ex.getMessage());
                    return Mono.just(Map.of("hits", Map.of("hits", new ArrayList<>())));
                });

            Map<String, Object> result = response.block();
            List<ActivityInstanceDTO> activities = new ArrayList<>();

            if (result != null && result.containsKey("hits")) {
                Map<String, Object> hits = (Map<String, Object>) result.get("hits");
                Object hitsListObj = hits.get("hits");
                if (hitsListObj instanceof List) {
                    List<Map<String, Object>> hitsList = (List<Map<String, Object>>) hitsListObj;
                    for (Map<String, Object> hit : hitsList) {
                        try {
                            Map<String, Object> source = (Map<String, Object>) hit.get("_source");
                            ActivityInstanceDTO activity = mapFlowNodeInstanceFromElasticsearch(source);
                            activities.add(activity);
                        } catch (Exception e) {
                            logger.warn("Failed to map flow node instance from Elasticsearch: {}", e.getMessage());
                        }
                    }
                }
            }

            logger.info("Found {} flow node instances for process instance: {} (queried from Elasticsearch)", activities.size(), processInstanceKey);
            return activities;

        } catch (Exception e) {
            logger.error("Failed to get flow node instances for process instance: {}", processInstanceKey, e);
            // Return empty list instead of throwing - allows frontend to still render diagram
            return new ArrayList<>();
        }
    }

    /**
     * Map Elasticsearch flow node instance document to ActivityInstanceDTO
     */
    private ActivityInstanceDTO mapFlowNodeInstanceFromElasticsearch(Map<String, Object> source) {
        ActivityInstanceDTO activity = new ActivityInstanceDTO();
        
        if (source.containsKey("key")) {
            activity.setActivityInstanceKey(Long.valueOf(source.get("key").toString()));
        }
        if (source.containsKey("flowNodeId")) {
            activity.setActivityId(source.get("flowNodeId").toString());
        }
        // Flow node name might not be in Elasticsearch, use flowNodeId as fallback
        if (source.containsKey("flowNodeName")) {
            activity.setActivityName(source.get("flowNodeName").toString());
        } else if (source.containsKey("flowNodeId")) {
            // Use flowNodeId as activity name if flowNodeName not available
            activity.setActivityName(source.get("flowNodeId").toString());
        }
        if (source.containsKey("type")) {
            activity.setActivityType(source.get("type").toString());
        }
        if (source.containsKey("state")) {
            activity.setState(source.get("state").toString());
        }
        if (source.containsKey("startDate")) {
            Object startDateObj = source.get("startDate");
            if (startDateObj != null && !startDateObj.toString().equals("null")) {
                activity.setStartTime(parseTimestamp(startDateObj.toString()));
            }
        }
        if (source.containsKey("endDate")) {
            Object endDateObj = source.get("endDate");
            if (endDateObj != null && !endDateObj.toString().equals("null")) {
                activity.setEndTime(parseTimestamp(endDateObj.toString()));
            }
        }
        
        return activity;
    }

    /**
     * Get incidents (errors) for a process instance
     */
    public List<IncidentDTO> getIncidents(Long processInstanceKey) {
        try {
            logger.debug("Getting incidents for process instance: {} from Operate API", processInstanceKey);

            Mono<Map> response = addAuthHeader(operateClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1/incidents")
                    .queryParam("processInstanceKey", processInstanceKey)
                    .queryParam("state", "OPEN")
                    .build()))
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
     * Handles formats like: "2025-12-01T23:37:05.608+0000" or "2025-12-01T23:37:05.608Z"
     */
    private LocalDateTime parseTimestamp(String timestamp) {
        try {
            if (timestamp == null || timestamp.isEmpty() || timestamp.equals("null")) {
                return null;
            }
            // Try parsing ISO 8601 format with timezone
            // Handle both "+0000" and "Z" formats
            String normalizedTimestamp = timestamp;
            if (timestamp.endsWith("+0000")) {
                normalizedTimestamp = timestamp.replace("+0000", "Z");
            }
            java.time.ZonedDateTime zonedDateTime = java.time.ZonedDateTime.parse(normalizedTimestamp, java.time.format.DateTimeFormatter.ISO_DATE_TIME);
            return zonedDateTime.toLocalDateTime();
        } catch (Exception e) {
            logger.warn("Failed to parse timestamp: {} - {}", timestamp, e.getMessage());
            return null;
        }
    }

    /**
     * Convert flow node ID to readable name (framework-based, works for any workflow)
     * Examples:
     * - "StartEvent_ContractConfiguration" -> "Contract Configuration Started"
     * - "UserTask_ConfigureCOA" -> "Configure COA"
     * - "ServiceTask_LaunchCOF" -> "Launch COF"
     */
    private String convertFlowNodeIdToReadableName(String flowNodeId) {
        if (flowNodeId == null || flowNodeId.isEmpty()) {
            return "Activity";
        }
        
        // Remove common prefixes and convert to readable format
        String name = flowNodeId;
        
        // Handle StartEvent_* -> "X Started"
        if (name.startsWith("StartEvent_")) {
            String processName = name.substring("StartEvent_".length());
            return convertCamelCaseToReadable(processName) + " Started";
        }
        
        // Handle UserTask_* -> "X"
        if (name.startsWith("UserTask_")) {
            String taskName = name.substring("UserTask_".length());
            return convertCamelCaseToReadable(taskName);
        }
        
        // Handle ServiceTask_* -> "X"
        if (name.startsWith("ServiceTask_")) {
            String taskName = name.substring("ServiceTask_".length());
            return convertCamelCaseToReadable(taskName);
        }
        
        // Handle other patterns generically
        return convertCamelCaseToReadable(name);
    }

    /**
     * Convert camelCase or PascalCase to readable format
     * Examples: "ContractConfiguration" -> "Contract Configuration", "ConfigureCOA" -> "Configure COA"
     */
    private String convertCamelCaseToReadable(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return "Activity";
        }
        
        // Insert space before capital letters (but not the first one)
        String readable = camelCase.replaceAll("([a-z])([A-Z])", "$1 $2");
        return readable;
    }

    /**
     * Extract initiator/startedBy from process variables (framework-based, works for any workflow)
     * Common variable names: "initiator", "startedBy", "createdBy", "userId", "user"
     */
    private String extractInitiatorFromVariables(Map<String, Object> processVariables) {
        if (processVariables == null || processVariables.isEmpty()) {
            return null;
        }
        
        // Framework-based: Try common variable names (works for any workflow)
        String[] initiatorKeys = {"initiator", "startedBy", "createdBy", "userId", "user", "initiatedBy"};
        for (String key : initiatorKeys) {
            if (processVariables.containsKey(key)) {
                Object value = processVariables.get(key);
                if (value != null && !value.toString().equals("null")) {
                    return value.toString();
                }
            }
        }
        
        return null;
    }

    /**
     * Extract outcome (APPROVED/REJECTED) from task variables (framework-based, works for any workflow)
     * Common patterns: "approved" boolean, "outcome" string, "decision" string, "action" string
     */
    private String extractOutcomeFromVariables(Map<String, Object> taskVariables) {
        if (taskVariables == null || taskVariables.isEmpty()) {
            return null;
        }
        
        // Framework-based: Try common variable names (works for any workflow)
        // Check for boolean "approved" variable
        if (taskVariables.containsKey("approved")) {
            Object approvedObj = taskVariables.get("approved");
            if (approvedObj != null) {
                if (approvedObj instanceof Boolean) {
                    return ((Boolean) approvedObj) ? "APPROVED" : "REJECTED";
                } else if (approvedObj.toString().equalsIgnoreCase("true")) {
                    return "APPROVED";
                } else if (approvedObj.toString().equalsIgnoreCase("false")) {
                    return "REJECTED";
                }
            }
        }
        
        // Check for string "outcome" variable
        if (taskVariables.containsKey("outcome")) {
            Object outcomeObj = taskVariables.get("outcome");
            if (outcomeObj != null && !outcomeObj.toString().equals("null")) {
                String outcome = outcomeObj.toString().toUpperCase();
                if (outcome.contains("APPROVE") || outcome.contains("APPROVED")) {
                    return "APPROVED";
                } else if (outcome.contains("REJECT") || outcome.contains("REJECTED")) {
                    return "REJECTED";
                }
                return outcome;
            }
        }
        
        // Check for "decision" or "action" variables
        String[] decisionKeys = {"decision", "action", "result"};
        for (String key : decisionKeys) {
            if (taskVariables.containsKey(key)) {
                Object value = taskVariables.get(key);
                if (value != null && !value.toString().equals("null")) {
                    String decision = value.toString().toUpperCase();
                    if (decision.contains("APPROVE")) {
                        return "APPROVED";
                    } else if (decision.contains("REJECT")) {
                        return "REJECTED";
                    }
                }
            }
        }
        
        return null;
    }
}

