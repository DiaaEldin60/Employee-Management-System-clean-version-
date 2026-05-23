package com.example.employee_management_system.repository;

import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.model.LeaveRequest;
import com.example.employee_management_system.model.LeaveRequest.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
    List<LeaveRequest> findByEmployee(Employees employee);
    List<LeaveRequest> findByStatus(LeaveStatus status);
    List<LeaveRequest> findByEmployeeAndStatus(Employees employee, LeaveStatus status);
    List<LeaveRequest> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
}
