package com.hhsa.workflow.config;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Zeebe (Camunda 8) client configuration.
 * Creates and configures the Zeebe client for workflow operations.
 */
@Configuration
public class ZeebeConfig {

    @Value("${zeebe.gateway.address}")
    private String gatewayAddress;

    @Value("${zeebe.client.max-jobs-active:32}")
    private int maxJobsActive;

    @Value("${zeebe.client.num-job-worker-execution-threads:1}")
    private int numJobWorkerExecutionThreads;

    @Bean
    public ZeebeClient zeebeClient() {
        ZeebeClientBuilder clientBuilder = ZeebeClient.newClientBuilder()
            .gatewayAddress(gatewayAddress)
            .usePlaintext(); // For local development; use TLS in production

        return clientBuilder.build();
    }
}




