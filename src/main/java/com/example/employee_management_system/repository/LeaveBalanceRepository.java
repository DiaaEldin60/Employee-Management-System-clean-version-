package com.example.employee_management_system.repository;

import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.model.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Integer> {
    Optional<LeaveBalance> findByEmployee(Employees employee);
    boolean existsByEmployee(Employees employee);
}
