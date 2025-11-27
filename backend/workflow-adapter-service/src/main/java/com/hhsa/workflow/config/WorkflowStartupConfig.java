package com.hhsa.workflow.config;

import com.hhsa.workflow.service.BpmnDeploymentService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for workflow startup tasks.
 * Deploys BPMN processes on application startup.
 */
@Configuration
public class WorkflowStartupConfig {

    private final BpmnDeploymentService bpmnDeploymentService;

    public WorkflowStartupConfig(BpmnDeploymentService bpmnDeploymentService) {
        this.bpmnDeploymentService = bpmnDeploymentService;
    }

    @PostConstruct
    public void deployBpmnProcesses() {
        bpmnDeploymentService.deployAllBpmnProcesses();
    }
}




