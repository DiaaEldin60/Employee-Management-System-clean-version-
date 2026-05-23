package com.example.employee_management_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import jakarta.annotation.PostConstruct;

/**
 * Application Configuration
 * Initializes and displays the active Spring profile at application startup.
 * Useful for debugging and verifying which environment the application is running in.
 */
@Configuration
@EnableConfigurationProperties
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${spring.application.name:employee-management-system}")
    private String applicationName;

    /**
     * Log the active profile on application startup
     */
    @PostConstruct
    public void init() {
        logger.info("========================================");
        logger.info("Application: {}", applicationName);
        logger.info("Active Profile: {}", activeProfile);
        logger.info("========================================");

        // Log different info based on profile
        if ("prod".equalsIgnoreCase(activeProfile)) {
            logger.warn("⚠️  PRODUCTION MODE - Using prod configuration");
            logger.warn("📍 Logs are stored in: {}", System.getProperty("logging.file.path", "stdout"));
        } else if ("dev".equalsIgnoreCase(activeProfile)) {
            logger.debug("✅ DEVELOPMENT MODE - Debug logging enabled");
            logger.debug("📍 Logs will appear in console");
        } else {
            logger.info("ℹ️  Profile: {}", activeProfile);
        }
    }
}

