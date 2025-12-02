package com.hhsa.contract.service;

import com.hhsa.contract.entity.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for integrating with Workflow Adapter Service.
 * Handles workflow process instance creation and management.
 */
@Service
public class WorkflowIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowIntegrationService.class);

    private final WebClient workflowClient;
    private final String workflowBaseUrl;

    // Workflow process definition keys
    private static final String WF302_PROCESS_KEY = "ContractConfiguration";
    private static final String WF303_PROCESS_KEY = "ContractCOF";

    public WorkflowIntegrationService(
            @Value("${services.workflow.base-url:http://localhost:8093}") String workflowBaseUrl) {
        this.workflowBaseUrl = workflowBaseUrl;
        this.workflowClient = WebClient.builder()
            .baseUrl(workflowBaseUrl)
            .build();
    }

    /**
     * Launch WF302: Contract Configuration workflow
     * Called after contract creation when COF is needed.
     */
    public String launchConfigurationWorkflow(Contract contract, String userId) {
        try {
            logger.info("Launching WF302 workflow for contract: {} (ID: {}), userId: {}", 
                contract.getContractNumber(), contract.getId(), userId);

            Map<String, Object> variables = new HashMap<>();
            variables.put("contractId", contract.getId());
            // Contract entity fields - adjust based on actual field names
            if (contract.getContractNumber() != null) {
                variables.put("contractNumber", contract.getContractNumber());
            }
            if (contract.getContractValue() != null) {
                variables.put("contractValue", contract.getContractValue().doubleValue());
            }
            variables.put("initiator", userId);
            logger.debug("WF302 workflow variables - initiator: {}, contractId: {}, contractNumber: {}", 
                userId, contract.getId(), contract.getContractNumber());
            if (contract.getOrganizationId() != null) {
                variables.put("organizationId", contract.getOrganizationId());
            }

            // Call workflow service to start process
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("processDefinitionKey", WF302_PROCESS_KEY);
            requestBody.put("variables", variables);

            // Real integration with Workflow Adapter Service
            Mono<Map> response = workflowClient.post()
                .uri("/api/v1/workflows/processes/{processDefinitionKey}/start", WF302_PROCESS_KEY)
                .headers(headers -> {
                    String token = getCurrentJwtToken();
                    if (token != null) {
                        headers.setBearerAuth(token);
                    }
                })
                .bodyValue(variables)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Workflow service error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                    return Mono.empty();
                });

            Map<String, Object> result = response.block();
            if (result != null && result.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) result.get("data");
                if (data.containsKey("processInstanceKey")) {
                    String processInstanceKey = String.valueOf(data.get("processInstanceKey"));
                    logger.info("✅ WF302 workflow launched successfully for contract: {} (ID: {}) - Process Instance Key: {}, Initiator: {}",
                        contract.getContractNumber(), contract.getId(), processInstanceKey, userId);
                    return processInstanceKey;
                } else {
                    logger.warn("⚠️ WF302 workflow launch response missing processInstanceKey for contract: {} - Response: {}", 
                        contract.getId(), result);
                }
            } else {
                logger.warn("⚠️ WF302 workflow launch returned null or missing data for contract: {} - Response: {}", 
                    contract.getId(), result);
            }

            logger.error("❌ WF302 workflow launch failed for contract: {} (ID: {}), userId: {}", 
                contract.getContractNumber(), contract.getId(), userId);
            return null;

        } catch (Exception e) {
            logger.error("Failed to launch WF302 workflow for contract: {}", contract.getId(), e);
            // Don't throw - workflow failure shouldn't block contract creation
            return null;
        }
    }

    /**
     * Launch WF303: Contract Certification of Funds (COF) workflow
     * Called after contract configuration is approved.
     */
    public String launchCOFWorkflow(Contract contract, String userId) {
        try {
            logger.info("Launching WF303 workflow for contract: {}", contract.getId());

            Map<String, Object> variables = new HashMap<>();
            variables.put("contractId", contract.getId());
            // Contract entity fields - adjust based on actual field names
            if (contract.getContractNumber() != null) {
                variables.put("contractNumber", contract.getContractNumber());
            }
            if (contract.getContractValue() != null) {
                variables.put("contractValue", contract.getContractValue().doubleValue());
            }
            variables.put("initiator", userId);
            if (contract.getOrganizationId() != null) {
                variables.put("organizationId", contract.getOrganizationId());
            }

            // Real integration with Workflow Adapter Service
            Mono<Map> response = workflowClient.post()
                .uri("/api/v1/workflows/processes/{processDefinitionKey}/start", WF303_PROCESS_KEY)
                .headers(headers -> {
                    String token = getCurrentJwtToken();
                    if (token != null) {
                        headers.setBearerAuth(token);
                    }
                })
                .bodyValue(variables)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Workflow service error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                    return Mono.empty();
                });

            Map<String, Object> result = response.block();
            if (result != null && result.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) result.get("data");
                if (data.containsKey("processInstanceKey")) {
                    String processInstanceKey = String.valueOf(data.get("processInstanceKey"));
                    logger.info("WF303 workflow launched successfully for contract: {} (instance: {})",
                        contract.getId(), processInstanceKey);
                    return processInstanceKey;
                }
            }

            logger.warn("WF303 workflow launch returned unexpected response for contract: {}", contract.getId());
            return null;

        } catch (Exception e) {
            logger.error("Failed to launch WF303 workflow for contract: {}", contract.getId(), e);
            return null;
        }
    }

    /**
     * Complete workflow task
     */
    public void completeWorkflowTask(Long taskId, Map<String, Object> variables, String userId) {
        try {
            logger.info("Completing workflow task: {} by user: {}", taskId, userId);

            Map<String, Object> requestBody = new HashMap<>();
            if (variables != null) {
                requestBody.putAll(variables);
            }
            requestBody.put("userId", userId);

            // Real integration with Workflow Adapter Service
            Mono<Map> response = workflowClient.post()
                .uri("/api/v1/workflows/tasks/{taskId}/complete", taskId)
                .headers(headers -> {
                    String token = getCurrentJwtToken();
                    if (token != null) {
                        headers.setBearerAuth(token);
                    }
                })
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Workflow service error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                    throw new RuntimeException("Failed to complete workflow task: " + ex.getMessage());
                });

            Map<String, Object> result = response.block();
            if (result != null && result.containsKey("success") && (Boolean) result.get("success")) {
                logger.info("Workflow task completed successfully: {}", taskId);
            } else {
                throw new RuntimeException("Task completion returned unsuccessful response");
            }

        } catch (Exception e) {
            logger.error("Failed to complete workflow task: {}", taskId, e);
            throw new RuntimeException("Failed to complete workflow task", e);
        }
    }

    /**
     * Get workflow tasks for a contract
     */
    public java.util.List<Map<String, Object>> getContractTasks(Long contractId) {
        try {
            logger.debug("Getting workflow tasks for contract: {}", contractId);

            // Query workflow service for tasks by process instance
            // First, we need to get the process instance keys from the contract
            // This would require injecting ContractRepository or ContractService
            // For now, query by contractId variable
            
            Mono<Map> response = workflowClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/api/v1/workflows/tasks")
                    .queryParam("contractId", contractId)
                    .build())
                .headers(headers -> {
                    String token = getCurrentJwtToken();
                    if (token != null) {
                        headers.setBearerAuth(token);
                    }
                })
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.warn("Could not query workflow tasks for contract: {}", contractId);
                    return Mono.just(Map.of("data", java.util.Collections.emptyList()));
                });

            Map<String, Object> result = response.block();
            if (result != null && result.containsKey("data")) {
                Object data = result.get("data");
                if (data instanceof java.util.List) {
                    return (java.util.List<Map<String, Object>>) data;
                }
            }

            return java.util.Collections.emptyList();

        } catch (Exception e) {
            logger.error("Failed to get workflow tasks for contract: {}", contractId, e);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Get the current JWT token from the security context
     */
    private String getCurrentJwtToken() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                return jwt.getTokenValue();
            }
        } catch (Exception e) {
            logger.warn("Failed to extract JWT token from security context", e);
        }
        return null;
    }
}

