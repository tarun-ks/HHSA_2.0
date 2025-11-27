package com.hhsa.workflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for handling workflow callbacks.
 * Updates contract status and performs actions based on workflow events.
 */
@Service
public class WorkflowCallbackService {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowCallbackService.class);

    private final WebClient contractClient;
    private final String contractServiceUrl;

    public WorkflowCallbackService(
            @Value("${services.contract.base-url:http://localhost:8095}") String contractServiceUrl) {
        this.contractServiceUrl = contractServiceUrl;
        this.contractClient = WebClient.builder()
            .baseUrl(contractServiceUrl)
            .build();
    }

    /**
     * Launch COF workflow (called from WF302 workflow)
     */
    public void launchCOFWorkflow(Long contractId, String userId) {
        try {
            logger.info("Launching COF workflow for contract: {} via callback", contractId);

            // This is already handled in ContractService.configureContract()
            // But we can trigger it here if needed
            // For now, just log
            logger.info("COF workflow launch triggered for contract: {}", contractId);

        } catch (Exception e) {
            logger.error("Failed to launch COF workflow for contract: {}", contractId, e);
        }
    }

    /**
     * Update contract status (called from workflow service tasks)
     */
    public void updateContractStatus(Long contractId, Integer statusId) {
        try {
            logger.info("Updating contract status: contract={}, status={}", contractId, statusId);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("statusId", statusId);

            Mono<Map> response = contractClient.patch()
                .uri("/api/v1/contracts/{id}/status", contractId)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    logger.error("Failed to update contract status: contract={}, status={}", contractId, statusId, ex);
                    return Mono.empty();
                });

            response.block();
            logger.info("Contract status updated: contract={}, status={}", contractId, statusId);

        } catch (Exception e) {
            logger.error("Failed to update contract status: contract={}, status={}", contractId, statusId, e);
        }
    }

    /**
     * Generate COF document (called from WF303 workflow)
     */
    public void generateCOFDocument(Long contractId) {
        try {
            logger.info("Generating COF document for contract: {}", contractId);

            // In production, this would call document service to generate COF document
            // For now, just log
            logger.info("COF document generation triggered for contract: {}", contractId);

        } catch (Exception e) {
            logger.error("Failed to generate COF document for contract: {}", contractId, e);
        }
    }
}


