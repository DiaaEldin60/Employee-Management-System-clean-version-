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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaveServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private LeaveService leaveService;

    private LeaveRequest testLeaveRequest;
    private Employees testEmployee;
    private LeaveBalance testLeaveBalance;
    private Users testUser;

    @BeforeEach
    void setUp() {
        testEmployee = new Employees();
        testEmployee.setId(1);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");

        testLeaveRequest = new LeaveRequest();
        testLeaveRequest.setId(1);
        testLeaveRequest.setEmployee(testEmployee);
        testLeaveRequest.setLeaveType(LeaveType.ANNUAL);
        testLeaveRequest.setStartDate(LocalDate.of(2024, 6, 1));
        testLeaveRequest.setEndDate(LocalDate.of(2024, 6, 5));
        testLeaveRequest.setStatus(LeaveStatus.PENDING);

        testLeaveBalance = new LeaveBalance(testEmployee);
        testLeaveBalance.setAnnualLeave(20);

        testUser = new Users();
        testUser.setId(1);
        testUser.setUserName("admin");
    }

    @Test
    void testCreateLeaveRequest() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(leaveBalanceRepository.findByEmployee(testEmployee)).thenReturn(Optional.of(testLeaveBalance));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(testLeaveRequest);

        LeaveRequest result = leaveService.createLeaveRequest(1, LeaveType.ANNUAL, 
                LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 5), "Vacation");

        assertNotNull(result);
        assertEquals(LeaveStatus.PENDING, result.getStatus());
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
    }

    @Test
    void testApproveLeaveRequest() {
        when(leaveRequestRepository.findById(1)).thenReturn(Optional.of(testLeaveRequest));
        when(leaveBalanceRepository.findByEmployee(testEmployee)).thenReturn(Optional.of(testLeaveBalance));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(testLeaveRequest);

        LeaveRequest result = leaveService.approveLeaveRequest(1, testUser);

        assertNotNull(result);
        assertEquals(LeaveStatus.APPROVED, result.getStatus());
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
    }

    @Test
    void testRejectLeaveRequest() {
        when(leaveRequestRepository.findById(1)).thenReturn(Optional.of(testLeaveRequest));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(testLeaveRequest);

        LeaveRequest result = leaveService.rejectLeaveRequest(1, testUser, "Not enough leave balance");

        assertNotNull(result);
        assertEquals(LeaveStatus.REJECTED, result.getStatus());
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
    }

    @Test
    void testGetLeaveRequestsByEmployee() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(leaveRequestRepository.findByEmployee(testEmployee)).thenReturn(List.of(testLeaveRequest));

        List<LeaveRequest> result = leaveService.getLeaveRequestsByEmployee(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(LeaveType.ANNUAL, result.get(0).getLeaveType());
        verify(leaveRequestRepository, times(1)).findByEmployee(testEmployee);
    }

    @Test
    void testGetPendingLeaveRequests() {
        when(leaveRequestRepository.findByStatus(LeaveStatus.PENDING)).thenReturn(List.of(testLeaveRequest));

        List<LeaveRequest> result = leaveService.getPendingLeaveRequests();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(LeaveStatus.PENDING, result.get(0).getStatus());
        verify(leaveRequestRepository, times(1)).findByStatus(LeaveStatus.PENDING);
    }
}
