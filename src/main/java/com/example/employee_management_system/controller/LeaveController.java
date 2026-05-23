package com.example.employee_management_system.controller;

import com.example.employee_management_system.dto.LeaveRequestDto;
import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.model.LeaveBalance;
import com.example.employee_management_system.model.LeaveRequest;
import com.example.employee_management_system.model.LeaveRequest.LeaveStatus;
import com.example.employee_management_system.model.LeaveRequest.LeaveType;
import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.repository.LeaveRequestRepository;
import com.example.employee_management_system.repository.UserRepository;
import com.example.employee_management_system.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leave")
@Tag(name = "Leave Management", description = "Leave management APIs")
public class LeaveController {

    private final LeaveService leaveService;
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;

    public LeaveController(LeaveService leaveService, LeaveRequestRepository leaveRequestRepository, UserRepository userRepository) {
        this.leaveService = leaveService;
        this.leaveRequestRepository = leaveRequestRepository;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Get leave balance by employee ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leave balance retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Employee not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @GetMapping("/balance/{employeeId}")
    public ResponseEntity<LeaveBalance> getLeaveBalance(@PathVariable int employeeId, Authentication authentication) {
        // Employees can only see their own balance
        if (!hasRole(authentication, "ADMIN") && !hasRole(authentication, "MANAGER")) {
            // EMPLOYEE can only see their own balance - need to verify employee belongs to user
        }
        LeaveBalance balance = leaveService.getLeaveBalanceByEmployeeId(employeeId);
        return ResponseEntity.ok(balance);
    }

    @Operation(summary = "Create leave request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leave request created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or insufficient leave balance"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @PostMapping("/requests")
    public ResponseEntity<LeaveRequest> createLeaveRequest(
            @RequestBody LeaveRequestDto dto,
            Authentication authentication) {
        Users user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Employees employee = user.getEmployees();
        if (employee == null) {
            throw new IllegalArgumentException("User is not associated with an employee");
        }
        LeaveRequest request = leaveService.createLeaveRequest(employee.getId(), dto.getLeaveType(), dto.getStartDate(), dto.getEndDate(), dto.getReason());
        return ResponseEntity.ok(request);
    }

    @Operation(summary = "Get my leave requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leave requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @GetMapping("/requests/my")
    public ResponseEntity<List<LeaveRequest>> getMyLeaveRequests(Authentication authentication) {
        Users user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Employees employee = user.getEmployees();
        if (employee == null) {
            return ResponseEntity.ok(List.of());
        }
        List<LeaveRequest> requests = leaveService.getLeaveRequestsByEmployee(employee.getId());
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Get all pending leave requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/requests/pending")
    public ResponseEntity<List<LeaveRequest>> getPendingLeaveRequests() {
        List<LeaveRequest> requests = leaveService.getPendingLeaveRequests();
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Get all leave requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/requests")
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        List<LeaveRequest> requests = leaveService.getAllLeaveRequests();
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Approve leave request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leave request approved successfully"),
        @ApiResponse(responseCode = "404", description = "Leave request not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/requests/{requestId}/approve")
    public ResponseEntity<LeaveRequest> approveLeaveRequest(
            @PathVariable int requestId,
            Authentication authentication) {
        Users approvedBy = getAuthenticatedUser(authentication);
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        // Managers can only be approved by ADMIN
        if (request.getEmployee().getUser() != null &&
            request.getEmployee().getUser().getRoles().stream()
                .anyMatch(role -> role.getRole().equals("MANAGER")) &&
            !hasRole(authentication, "ADMIN")) {
            throw new IllegalArgumentException("Manager leave requests can only be approved by ADMIN");
        }

        LeaveRequest approvedRequest = leaveService.approveLeaveRequest(requestId, approvedBy);
        return ResponseEntity.ok(approvedRequest);
    }

    @Operation(summary = "Reject leave request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leave request rejected successfully"),
        @ApiResponse(responseCode = "404", description = "Leave request not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<LeaveRequest> rejectLeaveRequest(
            @PathVariable int requestId,
            @RequestParam(required = false) String rejectionReason,
            Authentication authentication) {
        Users approvedBy = getAuthenticatedUser(authentication);
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        // Managers can only be approved/rejected by ADMIN
        if (request.getEmployee().getUser() != null &&
            request.getEmployee().getUser().getRoles().stream()
                .anyMatch(role -> role.getRole().equals("MANAGER")) &&
            !hasRole(authentication, "ADMIN")) {
            throw new IllegalArgumentException("Manager leave requests can only be approved/rejected by ADMIN");
        }

        LeaveRequest rejectedRequest = leaveService.rejectLeaveRequest(requestId, approvedBy, rejectionReason);
        return ResponseEntity.ok(rejectedRequest);
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }

    private Users getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
