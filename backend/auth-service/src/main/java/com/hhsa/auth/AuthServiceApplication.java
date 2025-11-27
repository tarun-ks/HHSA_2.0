package com.hhsa.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Auth Service Application
 * Provides authentication and authorization using Keycloak ONLY.
 */
@SpringBootApplication(scanBasePackages = {"com.hhsa.auth", "com.hhsa.common.core"})
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}




