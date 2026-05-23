package com.example.employee_management_system.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Email Test Controller - FOR TESTING & DEBUGGING ONLY
 * 
 * This controller is placed in the test folder to avoid exposure in production.
 * It provides endpoints to test Brevo email service integration and diagnose
 * email configuration issues during development and staging.
 * 
 * Endpoints:
 * - GET /test-brevo-key - Verify API key configuration
 * - GET /test-send-email - Send a test email to verify connectivity
 * 
 * WARNING: This should only be accessible in development/staging environments.
 * Consider securing with authentication in those environments.
 */
@RestController
public class EmailTestController {

    private static final Logger logger = LoggerFactory.getLogger(EmailTestController.class);
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private static final String BREVO_ACCOUNT_URL = "https://api.brevo.com/v3/account";

    private final RestTemplate restTemplate;

    /// BY ME
    /// @Value is used to inject values from application.properties
    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${brevo.from.email:noreply@example.com}")
    private String fromEmail;

    public EmailTestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/test-brevo-key")
    public Map<String, Object> testBrevoKey() {
        Map<String, Object> result = new HashMap<>();
        
        String apiKey = brevoApiKey != null ? brevoApiKey.trim() : "";
        result.put("apiKeyLength", apiKey.length());
        result.put("apiKeyPrefix", apiKey.length() > 15 ? apiKey.substring(0, 15) + "..." : "EMPTY");
        result.put("fromEmail", fromEmail);
        
        if (apiKey.isEmpty()) {
            result.put("status", "ERROR");
            result.put("message", "API key is empty or not configured");
            return result;
        }
        
        // Test 1: Check account info
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    BREVO_ACCOUNT_URL,
                    HttpMethod.GET,
                    request,
                    String.class
            );
            
            result.put("accountCheck", "SUCCESS");
            result.put("accountResponse", response.getBody());
        } catch (Exception e) {
            result.put("accountCheck", "FAILED");
            result.put("accountError", e.getMessage());
            logger.error("Account check failed: {}", e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/test-send-email")
    public Map<String, Object> testSendEmail(@RequestParam String to) {
        Map<String, Object> result = new HashMap<>();
        
        String apiKey = brevoApiKey != null ? brevoApiKey.trim() : "";
        
        if (apiKey.isEmpty()) {
            result.put("status", "ERROR");
            result.put("message", "API key is empty");
            return result;
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            
            Map<String, Object> sender = new HashMap<>();
            sender.put("email", fromEmail);
            sender.put("name", "EMS Test");
            
            List<Map<String, Object>> toList = new ArrayList<>();
            Map<String, Object> recipient = new HashMap<>();
            recipient.put("email", to);
            toList.add(recipient);
            
            Map<String, Object> emailRequest = new HashMap<>();
            emailRequest.put("sender", sender);
            emailRequest.put("to", toList);
            emailRequest.put("subject", "Test Email from EMS");
            emailRequest.put("htmlContent", "<html><body><h1>Test Email</h1><p>This is a test from your EMS application.</p></body></html>");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailRequest, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    BREVO_API_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            
            result.put("status", "SUCCESS");
            result.put("responseStatus", response.getStatusCode().toString());
            result.put("responseBody", response.getBody());
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            result.put("status", "FAILED");
            result.put("error", "401 UNAUTHORIZED");
            result.put("errorDetails", e.getResponseBodyAsString());
            result.put("solution", "Go to https://app.brevo.com/settings/keys/api and ensure your key has 'Transactional emails' permission");
        } catch (Exception e) {
            result.put("status", "FAILED");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}

