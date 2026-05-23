package com.example.employee_management_system.service;

import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.model.LeaveBalance;
import com.example.employee_management_system.model.LeaveRequest;
import com.example.employee_management_system.model.LeaveRequest.LeaveStatus;
import com.example.employee_management_system.model.LeaveRequest.LeaveType;
import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.repository.EmployeeRepository;
import com.example.employee_management_system.repository.LeaveBalanceRepository;
import com.example.employee_management_system.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveService(LeaveBalanceRepository leaveBalanceRepository,
                        LeaveRequestRepository leaveRequestRepository,
                        EmployeeRepository employeeRepository) {
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public LeaveBalance getOrCreateLeaveBalance(Employees employee) {
        return leaveBalanceRepository.findByEmployee(employee)
                .orElseGet(() -> {
                    LeaveBalance balance = new LeaveBalance(employee);
                    return leaveBalanceRepository.save(balance);
                });
    }

    @Transactional(readOnly = true)
    public LeaveBalance getLeaveBalanceByEmployeeId(int employeeId) {
        Employees employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return getOrCreateLeaveBalance(employee);
    }

    @Transactional
    public LeaveRequest createLeaveRequest(int employeeId, LeaveType leaveType,
                                           LocalDate startDate, LocalDate endDate, String reason) {
        Employees employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        LeaveBalance balance = getOrCreateLeaveBalance(employee);

        // Calculate days
        int days = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Validate leave balance
        if (!hasSufficientLeave(balance, leaveType, days)) {
            throw new IllegalArgumentException("Insufficient leave balance");
        }

        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setLeaveType(leaveType);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setDaysCount(days);
        request.setReason(reason);
        request.setStatus(LeaveStatus.PENDING);

        return leaveRequestRepository.save(request);
    }

    @Transactional
    public LeaveRequest approveLeaveRequest(int requestId, Users approvedBy) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        LeaveBalance balance = getOrCreateLeaveBalance(request.getEmployee());

        // Deduct leave balance
        deductLeave(balance, request.getLeaveType(), request.getDaysCount());

        request.setStatus(LeaveStatus.APPROVED);
        request.setApprovedBy(approvedBy);
        request.setApprovedAt(LocalDateTime.now());

        return leaveRequestRepository.save(request);
    }

    @Transactional
    public LeaveRequest rejectLeaveRequest(int requestId, Users approvedBy, String rejectionReason) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        request.setStatus(LeaveStatus.REJECTED);
        request.setApprovedBy(approvedBy);
        request.setApprovedAt(LocalDateTime.now());
        request.setRejectionReason(rejectionReason);

        return leaveRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> getLeaveRequestsByEmployee(int employeeId) {
        Employees employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return leaveRequestRepository.findByEmployee(employee);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> getPendingLeaveRequests() {
        return leaveRequestRepository.findByStatus(LeaveStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    private boolean hasSufficientLeave(LeaveBalance balance, LeaveType type, int days) {
        switch (type) {
            case ANNUAL:
                return balance.getAnnualLeave() >= days;
            case EMERGENCY:
                return balance.getEmergencyLeave() >= days;
            case SICK:
                return balance.getSickLeave() >= days;
            default:
                return false;
        }
    }

    private void deductLeave(LeaveBalance balance, LeaveType type, int days) {
        switch (type) {
            case ANNUAL:
                balance.setAnnualLeave(balance.getAnnualLeave() - days);
                break;
            case EMERGENCY:
                balance.setEmergencyLeave(balance.getEmergencyLeave() - days);
                break;
            case SICK:
                balance.setSickLeave(balance.getSickLeave() - days);
                break;
        }
        leaveBalanceRepository.save(balance);
    }
}
