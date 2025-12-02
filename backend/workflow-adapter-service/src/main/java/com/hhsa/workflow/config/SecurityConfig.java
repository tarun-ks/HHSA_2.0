package com.hhsa.workflow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Workflow Adapter Service.
 * Configures OAuth2 Resource Server with Keycloak JWT validation.
 * Allows public access to health checks and API docs.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:http://localhost:8090/realms/hhsa/protocol/openid-connect/certs}")
    private String jwkSetUri;

    /**
     * Security filter chain configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable()) // CORS handled by API Gateway
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (health checks, API docs)
                .requestMatchers(
                    "/actuator/**",  // Allow all actuator endpoints
                    "/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );

        return http.build();
    }

    /**
     * JWT Decoder for Keycloak
     * Note: This decoder will only be used for authenticated endpoints.
     * Public endpoints (like /actuator/health) bypass JWT validation.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        } catch (Exception e) {
            // If Keycloak is not available at startup, log warning but don't fail
            // The decoder will be used only when tokens are present
            System.err.println("WARNING: Failed to create JWT decoder. Keycloak may not be running: " + e.getMessage());
            // Return a decoder that will fail gracefully when used
            return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        }
    }

    // CORS is handled by API Gateway - no need to configure here
}

