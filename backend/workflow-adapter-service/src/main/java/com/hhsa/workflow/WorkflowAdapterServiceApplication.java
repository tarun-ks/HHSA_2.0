package com.hhsa.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Workflow Adapter Service Application
 * Provides workflow integration with Camunda 8 (Zeebe) for BPMN and DMN.
 */
@SpringBootApplication(scanBasePackages = {"com.hhsa.workflow", "com.hhsa.common.core"})
public class WorkflowAdapterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowAdapterServiceApplication.class, args);
    }
}




