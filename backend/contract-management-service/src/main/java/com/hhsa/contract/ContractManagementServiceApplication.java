package com.hhsa.contract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Contract Management Service Application
 * Handles Contract Financials and Contract Budget features.
 */
@SpringBootApplication(scanBasePackages = {"com.hhsa.contract", "com.hhsa.common.core"})
@EntityScan(basePackages = {"com.hhsa.contract.entity", "com.hhsa.common.core.entity"})
@EnableJpaRepositories(basePackages = {"com.hhsa.contract.repository", "com.hhsa.common.core.repository"})
public class ContractManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractManagementServiceApplication.class, args);
    }
}




