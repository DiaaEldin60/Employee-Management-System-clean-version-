package com.example.employee_management_system.controller;

import com.example.employee_management_system.annotation.RateLimited;
import com.example.employee_management_system.dto.LoginRequest;
import com.example.employee_management_system.dto.RegisterEmployeeRequest;
import com.example.employee_management_system.dto.AuthResponse;
import com.example.employee_management_system.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    @RateLimited(limit = 3, period = 3600) // 3 requests per hour
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterEmployeeRequest registerRequest) {
        authenticationService.registerEmployeeToUser(
            registerRequest.getUsername(),
            registerRequest.getPassword(),
            registerRequest.getEmployeeId()
        );
        return ResponseEntity.ok(new AuthResponse("User registered with default EMPLOYEE role", true));
    }

    @Operation(summary = "Login user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    @RateLimited(limit = 5, period = 60) // 5 requests per minute
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String message = authenticationService.login(loginRequest.getUsername(), loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(new AuthResponse(message, true));
    }
}
