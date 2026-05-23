package com.example.employee_management_system.controller;

import com.example.employee_management_system.annotation.RateLimited;
import com.example.employee_management_system.dto.AuthResponse;
import com.example.employee_management_system.dto.PasswordResetConfirmRequest;
import com.example.employee_management_system.dto.PasswordResetRequest;
import com.example.employee_management_system.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/password-reset")
@Tag(name = "Password Reset", description = "Password reset management APIs")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @Operation(summary = "Request password reset")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "No account found with this email"),
        @ApiResponse(responseCode = "409", description = "Password reset email already sent")
    })
    @PostMapping("/request")
    @RateLimited(limit = 3, period = 3600) // 3 requests per hour
    public ResponseEntity<AuthResponse> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.createAndSendPasswordResetToken(request.getEmail());
        return ResponseEntity.ok(new AuthResponse("Password reset email sent successfully", true));
    }

    @Operation(summary = "Validate reset code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reset code is valid"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired code")
    })
    @PostMapping("/validate")
    @RateLimited(limit = 10, period = 60) // 10 requests per minute
    public ResponseEntity<AuthResponse> validateResetToken(@RequestParam String code) {
        passwordResetService.validateResetToken(code);
        return ResponseEntity.ok(new AuthResponse("Reset code is valid", true));
    }

    @Operation(summary = "Confirm password reset")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or code"),
        @ApiResponse(responseCode = "404", description = "Invalid code")
    })
    @PostMapping("/confirm")
    @RateLimited(limit = 5, period = 60) // 5 requests per minute
    public ResponseEntity<AuthResponse> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        passwordResetService.resetPassword(request.getCode(), request.getNewPassword());
        return ResponseEntity.ok(new AuthResponse("Password reset successfully", true));
    }
}
