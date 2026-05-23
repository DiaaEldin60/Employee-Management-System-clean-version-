package com.example.employee_management_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailVerificationRequest {
    
    @NotBlank(message = "Username is required")
    private String username;

    public EmailVerificationRequest() {}

    public EmailVerificationRequest(String username) {
        this.username = username;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
