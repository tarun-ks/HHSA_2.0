package com.hhsa.workflow.service;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.api.worker.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Map;

/**
 * Job worker service for handling service tasks in BPMN processes.
 * Handles service tasks like "launch-cof-workflow", "update-contract-status", etc.
 */
@Service
public class JobWorkerService {

    private static final Logger logger = LoggerFactory.getLogger(JobWorkerService.class);

    private final ZeebeClient zeebeClient;
    private final WorkflowCallbackService callbackService;
    private final int maxJobsActive;
    private JobWorker launchCofWorker;
    private JobWorker updateStatusWorker;
    private JobWorker generateDocumentWorker;

    public JobWorkerService(
            ZeebeClient zeebeClient,
            WorkflowCallbackService callbackService,
            @Value("${zeebe.client.max-jobs-active:32}") int maxJobsActive) {
        this.zeebeClient = zeebeClient;
        this.callbackService = callbackService;
        this.maxJobsActive = maxJobsActive;
    }

    @PostConstruct
    public void startWorkers() {
        logger.info("Starting Zeebe job workers...");

        // Worker for launching COF workflow
        launchCofWorker = zeebeClient.newWorker()
            .jobType("launch-cof-workflow")
            .handler(new LaunchCofWorkflowHandler())
            .name("launch-cof-worker")
            .maxJobsActive(maxJobsActive)
            .open();

        // Worker for updating contract status
        updateStatusWorker = zeebeClient.newWorker()
            .jobType("update-contract-status")
            .handler(new UpdateContractStatusHandler())
            .name("update-status-worker")
            .maxJobsActive(maxJobsActive)
            .open();

        // Worker for generating COF document
        generateDocumentWorker = zeebeClient.newWorker()
            .jobType("generate-cof-document")
            .handler(new GenerateDocumentHandler())
            .name("generate-document-worker")
            .maxJobsActive(maxJobsActive)
            .open();

        logger.info("Zeebe job workers started");
    }

    @PreDestroy
    public void stopWorkers() {
        logger.info("Stopping Zeebe job workers...");
        if (launchCofWorker != null) {
            launchCofWorker.close();
        }
        if (updateStatusWorker != null) {
            updateStatusWorker.close();
        }
        if (generateDocumentWorker != null) {
            generateDocumentWorker.close();
        }
        logger.info("Zeebe job workers stopped");
    }

    /**
     * Handler for launching COF workflow
     */
    private class LaunchCofWorkflowHandler implements JobHandler {
        @Override
        public void handle(JobClient client, ActivatedJob job) {
            try {
                logger.info("Handling launch-cof-workflow job: {}", job.getKey());

                Map<String, Object> variables = job.getVariablesAsMap();
                Long contractId = Long.valueOf(variables.get("contractId").toString());
                String userId = variables.get("initiator").toString();

                // Launch WF303 workflow via callback service
                callbackService.launchCOFWorkflow(contractId, userId);

                // Complete the job
                client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send();

                logger.info("COF workflow launched for contract: {}", contractId);

            } catch (Exception e) {
                logger.error("Failed to handle launch-cof-workflow job: {}", job.getKey(), e);
                client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send();
            }
        }
    }

    /**
     * Handler for updating contract status
     */
    private class UpdateContractStatusHandler implements JobHandler {
        @Override
        public void handle(JobClient client, ActivatedJob job) {
            try {
                logger.info("Handling update-contract-status job: {}", job.getKey());

                Map<String, Object> variables = job.getVariablesAsMap();
                Long contractId = Long.valueOf(variables.get("contractId").toString());
                Boolean approved = variables.containsKey("approved") ? 
                    Boolean.valueOf(variables.get("approved").toString()) : null;

                // Update contract status via callback service
                if (approved != null) {
                    if (approved) {
                        callbackService.updateContractStatus(contractId, 61); // Pending Registration
                    } else {
                        callbackService.updateContractStatus(contractId, 69); // Cancelled
                    }
                }

                // Complete the job
                client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send();

                logger.info("Contract status updated for contract: {} (approved: {})", contractId, approved);

            } catch (Exception e) {
                logger.error("Failed to handle update-contract-status job: {}", job.getKey(), e);
                client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send();
            }
        }
    }

    /**
     * Handler for generating COF document
     */
    private class GenerateDocumentHandler implements JobHandler {
        @Override
        public void handle(JobClient client, ActivatedJob job) {
            try {
                logger.info("Handling generate-cof-document job: {}", job.getKey());

                Map<String, Object> variables = job.getVariablesAsMap();
                Long contractId = Long.valueOf(variables.get("contractId").toString());

                // Generate COF document via callback service
                callbackService.generateCOFDocument(contractId);

                // Complete the job
                client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send();

                logger.info("COF document generated for contract: {}", contractId);

            } catch (Exception e) {
                logger.error("Failed to handle generate-cof-document job: {}", job.getKey(), e);
                client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send();
            }
        }
    }
}

