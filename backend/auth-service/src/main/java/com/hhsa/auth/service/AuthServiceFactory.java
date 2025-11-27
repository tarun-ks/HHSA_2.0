package com.hhsa.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Factory for authentication service.
 * Uses Keycloak ONLY for authentication.
 */
@Component
public class AuthServiceFactory {

    private final AuthService keycloakAuthService;

    @Autowired
    public AuthServiceFactory(
            @Qualifier("keycloakAuthService") AuthService keycloakAuthService) {
        this.keycloakAuthService = keycloakAuthService;
    }

    /**
     * Get authentication service.
     * Always returns Keycloak auth service.
     */
    public AuthService getAuthService(String provider) {
        // Only Keycloak is supported
        return keycloakAuthService;
    }

    /**
     * Get default authentication service
     */
    public AuthService getDefaultAuthService() {
        return keycloakAuthService;
    }
}




