package com.hhsa.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Document Service Application
 * Provides document management with pluggable storage (local file system for POC, S3 for future).
 */
@SpringBootApplication(scanBasePackages = {"com.hhsa.document", "com.hhsa.common.core"})
@EntityScan(basePackages = {"com.hhsa.document.entity", "com.hhsa.common.core.entity"})
@EnableJpaRepositories(basePackages = {"com.hhsa.document.repository", "com.hhsa.common.core.repository"})
public class DocumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentServiceApplication.class, args);
    }
}




