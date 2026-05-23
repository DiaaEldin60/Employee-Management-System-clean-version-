package com.example.employee_management_system.controller;

import com.example.employee_management_system.annotation.RateLimited;
import com.example.employee_management_system.dto.AuthResponse;
import com.example.employee_management_system.dto.EmailVerificationRequest;
import com.example.employee_management_system.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email-verification")
@Tag(name = "Email Verification", description = "Email verification management APIs")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @Operation(summary = "Send email verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verification email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Verification email already sent")
    })
    @PostMapping("/send")
    /// BY ME
    /// @RateLimited is used to limit the number of requests per minute
    @RateLimited(limit = 5, period = 3600) // 5 requests per hour
    public ResponseEntity<AuthResponse> sendVerificationEmail(@Valid @RequestBody EmailVerificationRequest request) {
        emailVerificationService.createAndSendVerificationToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse("Verification email sent successfully", true));
    }

    @Operation(summary = "Resend email verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verification email resent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/resend")
    @RateLimited(limit = 3, period = 3600) // 3 requests per hour
    public ResponseEntity<AuthResponse> resendVerificationEmail(@Valid @RequestBody EmailVerificationRequest request) {
        emailVerificationService.resendVerificationEmail(request.getUsername());
        return ResponseEntity.ok(new AuthResponse("Verification email resent successfully", true));
    }

    @Operation(summary = "Verify email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
        @ApiResponse(responseCode = "409", description = "Email already verified")
    })
    @PostMapping("/verify")
    @RateLimited(limit = 10, period = 60) // 10 requests per minute
    public ResponseEntity<AuthResponse> verifyEmail(@RequestParam String code) {
        emailVerificationService.verifyEmail(code);
        return ResponseEntity.ok(new AuthResponse("Email verified successfully", true));
    }

    @Operation(summary = "Check if email is verified")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verification status retrieved"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/status")
    @RateLimited(limit = 20, period = 60) // 20 requests per minute
    public ResponseEntity<AuthResponse> checkEmailVerificationStatus(@RequestParam String username) {
        boolean isVerified = emailVerificationService.isEmailVerified(username);
        String message = isVerified ? "Email is verified" : "Email is not verified";
        return ResponseEntity.ok(new AuthResponse(message, isVerified));
    }
}
