package com.example.employee_management_system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${brevo.from.email:noreply@example.com}")
    private String fromEmail;

    @Value("${app.frontend.url:https://employee-management-system-production-2a1a.up.railway.app}")
    private String frontendUrl;

    @Autowired
    public EmailService(RestTemplate restTemplate, TemplateEngine templateEngine) {
        this.restTemplate = restTemplate;
        this.templateEngine = templateEngine;
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            // Trim API key and log length for debugging (don't log full key for security)
            String apiKey = brevoApiKey != null ? brevoApiKey.trim() : "";
            logger.info("Sending email to: {}, API key length: {}, API key starts with: {}",
                    to, apiKey.length(), apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : "EMPTY");

            if (apiKey.isEmpty()) {
                throw new RuntimeException("Brevo API key is not configured");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);  // Brevo uses lowercase 'api-key' header
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");

            Map<String, Object> sender = new HashMap<>();
            sender.put("email", fromEmail);
            sender.put("name", "Employee Management System");

            List<Map<String, Object>> toList = new ArrayList<>();
            Map<String, Object> recipient = new HashMap<>();
            recipient.put("email", to);
            toList.add(recipient);

            Map<String, Object> emailRequest = new HashMap<>();
            emailRequest.put("sender", sender);
            emailRequest.put("to", toList);
            emailRequest.put("subject", subject);
            emailRequest.put("htmlContent", htmlContent);

            logger.info("Email request payload: sender={}, to={}, subject={}", sender, to, subject);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailRequest, headers);

            logger.info("Sending request to Brevo API...");

            ResponseEntity<String> response = restTemplate.exchange(
                    BREVO_API_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            logger.info("Brevo API response status: {}", response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Brevo API error response: {}", response.getBody());
                throw new RuntimeException("Failed to send email: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            logger.error("Brevo API 401 UNAUTHORIZED. Full response: {}", e.getResponseBodyAsString());
            logger.error("Request headers were: api-key=[HIDDEN], Content-Type=application/json");
            logger.error("This usually means: 1) API key is invalid, 2) Key lacks 'Transactional emails' permission, or 3) Sender email not verified");
            throw new RuntimeException("Brevo 401: API key invalid or missing permissions. Check: https://app.brevo.com/settings/keys/api", e);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            logger.error("Brevo API client error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to send email: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Failed to send email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
    /// by me
    /// @Async is used to send emails asynchronously, improving performance by not blocking the main thread.
    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        String htmlContent = "<html><body><p>" + text.replace("\n", "<br/>") + "</p></body></html>";
        sendEmail(to, subject, htmlContent);
    }

    @Async
    public void sendHtmlMessage(String to, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process(templateName, context);
        sendEmail(to, subject, htmlContent);
    }

    @Async
    public void sendEmailVerification(String to, String verificationCode, String userName) {
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("verificationCode", verificationCode);
        context.setVariable("supportEmail", fromEmail);
        context.setVariable("frontendUrl", frontendUrl);

        String htmlContent = templateEngine.process("email-verification", context);
        sendEmail(to, "Verify Your Email Address", htmlContent);
    }

    @Async
    public void sendPasswordResetEmail(String to, String resetCode, String userName) {
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("resetCode", resetCode);
        context.setVariable("supportEmail", fromEmail);
        context.setVariable("expiryHours", "24");
        context.setVariable("frontendUrl", frontendUrl);

        String htmlContent = templateEngine.process("password-reset", context);
        sendEmail(to, "Reset Your Password", htmlContent);
    }

    @Async
    public void sendPasswordChangedNotification(String to, String userName) {
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("supportEmail", fromEmail);
        context.setVariable("frontendUrl", frontendUrl);

        String htmlContent = templateEngine.process("password-changed", context);
        sendEmail(to, "Your Password Has Been Changed", htmlContent);
    }
}
