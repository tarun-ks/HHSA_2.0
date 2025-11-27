package com.hhsa.contract.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for Contract Management Service.
 * Note: CORS is configured in SecurityConfig since we're using Spring Security.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS configuration is handled by SecurityConfig.corsConfigurationSource()
    // No additional web configuration needed
}



