package com.example.employee_management_system.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP Client Configuration
 * Configures RestTemplate bean for making HTTP requests to external services.
 * Used by: EmailService, EmailTestController, and other services that need to communicate with external APIs.
 */
@Configuration
public class HttpClientConfig {

    /**
     * RestTemplate bean for making HTTP requests
     * @param builder RestTemplateBuilder for building RestTemplate with custom configurations
     * @return configured RestTemplate
     */
    /// by me
    /// RestTemplate - used to make HTTP requests is it used by EmailService, EmailTestController, and other services that need to communicate with external APIs
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

