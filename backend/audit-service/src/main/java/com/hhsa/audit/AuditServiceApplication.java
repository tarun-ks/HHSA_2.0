package com.hhsa.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Audit Service Application
 * Provides audit logging using OpenTelemetry for all create, update, delete operations and workflow state changes.
 */
@SpringBootApplication(scanBasePackages = {"com.hhsa.audit", "com.hhsa.common.core"})
@EntityScan(basePackages = {"com.hhsa.audit.entity", "com.hhsa.common.core.entity"})
@EnableJpaRepositories(basePackages = {"com.hhsa.audit.repository", "com.hhsa.common.core.repository"})
public class AuditServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuditServiceApplication.class, args);
    }
}




