package com.hhsa.workflow.service;

import com.hhsa.workflow.adapter.WorkflowException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Service for deploying BPMN process definitions to Camunda 8.
 * Automatically deploys BPMN files on application startup.
 */
@Service
public class BpmnDeploymentService {

    private static final Logger logger = LoggerFactory.getLogger(BpmnDeploymentService.class);

    private final ZeebeClient zeebeClient;
    private final ResourceLoader resourceLoader;

    public BpmnDeploymentService(ZeebeClient zeebeClient, ResourceLoader resourceLoader) {
        this.zeebeClient = zeebeClient;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Deploy BPMN process definition from classpath
     */
    public void deployBpmnProcess(String bpmnResourcePath) {
        try {
            logger.info("Deploying BPMN process from: {}", bpmnResourcePath);

            Resource resource = resourceLoader.getResource("classpath:" + bpmnResourcePath);
            if (!resource.exists()) {
                logger.warn("BPMN resource not found: {}", bpmnResourcePath);
                return;
            }

            String bpmnXml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            // Extract filename from resource path
            String filename = Paths.get(bpmnResourcePath).getFileName().toString();

            DeploymentEvent deployment = zeebeClient.newDeployResourceCommand()
                .addResourceString(bpmnXml, StandardCharsets.UTF_8, filename)
                .send()
                .join();

            logger.info("BPMN process deployed successfully: {} (deployment key: {})",
                bpmnResourcePath, deployment.getKey());

            // Log deployed processes
            deployment.getProcesses().forEach(process -> {
                logger.info("  - Process: {} (key: {}, version: {})",
                    process.getBpmnProcessId(),
                    process.getProcessDefinitionKey(),
                    process.getVersion());
            });

        } catch (IOException e) {
            logger.error("Failed to read BPMN resource: {}", bpmnResourcePath, e);
        } catch (Exception e) {
            logger.error("Failed to deploy BPMN process: {}", bpmnResourcePath, e);
        }
    }

    /**
     * Deploy all BPMN processes on startup
     */
    public void deployAllBpmnProcesses() {
        logger.info("Starting BPMN process deployment...");

        // Deploy WF302: Contract Configuration
        deployBpmnProcess("bpmn/WF302_ContractConfiguration.bpmn");

        // Deploy WF303: Contract COF
        deployBpmnProcess("bpmn/WF303_ContractCOF.bpmn");

        logger.info("BPMN process deployment completed");
    }

    /**
     * Get BPMN XML for a process definition by process definition key (BPMN process ID).
     * Framework-based: works for any workflow.
     * 
     * @param processDefinitionKey BPMN process ID (e.g., "ContractConfiguration", "ContractCOF")
     * @return BPMN XML content
     * @throws WorkflowException if BPMN not found
     */
    public String getBpmnXml(String processDefinitionKey) throws WorkflowException {
        try {
            logger.debug("Getting BPMN XML for process definition: {}", processDefinitionKey);

            // Map process definition key to BPMN resource path
            // Framework-based: add new workflows here
            String bpmnResourcePath = getBpmnResourcePath(processDefinitionKey);
            
            if (bpmnResourcePath == null) {
                throw new WorkflowException("BPMN process definition not found: " + processDefinitionKey);
            }

            Resource resource = resourceLoader.getResource("classpath:" + bpmnResourcePath);
            if (!resource.exists()) {
                throw new WorkflowException("BPMN resource not found: " + bpmnResourcePath);
            }

            String bpmnXml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            logger.debug("Retrieved BPMN XML for process definition: {} (size: {} bytes)", 
                processDefinitionKey, bpmnXml.length());
            
            return bpmnXml;

        } catch (IOException e) {
            logger.error("Failed to read BPMN resource for process definition: {}", processDefinitionKey, e);
            throw new WorkflowException("Failed to read BPMN XML: " + e.getMessage(), e);
        } catch (WorkflowException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to get BPMN XML for process definition: {}", processDefinitionKey, e);
            throw new WorkflowException("Failed to get BPMN XML: " + e.getMessage(), e);
        }
    }

    /**
     * Map process definition key (BPMN process ID) to BPMN resource path.
     * Framework-based: add new workflows here.
     */
    private String getBpmnResourcePath(String processDefinitionKey) {
        // Framework-based mapping - add new workflows here
        switch (processDefinitionKey) {
            case "ContractConfiguration":
                return "bpmn/WF302_ContractConfiguration.bpmn";
            case "ContractCOF":
                return "bpmn/WF303_ContractCOF.bpmn";
            default:
                logger.warn("Unknown process definition key: {}", processDefinitionKey);
                return null;
        }
    }
}

