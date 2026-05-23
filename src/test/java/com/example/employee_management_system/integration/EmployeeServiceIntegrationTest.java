package com.example.employee_management_system.integration;

import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.repository.EmployeeRepository;
import com.example.employee_management_system.repository.LeaveBalanceRepository;
import com.example.employee_management_system.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.example.employee_management_system.config.TestSecurityConfig;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class EmployeeServiceIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @BeforeEach
    void setUp() {
        leaveBalanceRepository.deleteAllInBatch();
        employeeRepository.deleteAllInBatch();
    }

    @Test
    void testCreateEmployee() {
        Employees employee = new Employees();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");
        employee.setPhoneNumber("1234567890");
        employee.setHireDate(LocalDate.now());
        employee.setJobTitle("Developer");
        employee.setSalary(50000.0f);
        employee.setDepartment("IT");

        Employees savedEmployee = employeeService.createEmployee(employee);

        assertNotNull(savedEmployee.getId());
        assertEquals("John", savedEmployee.getFirstName());
        assertEquals("Doe", savedEmployee.getLastName());
        assertEquals("john.doe@example.com", savedEmployee.getEmail());
    }

    @Test
    void testGetEmployeeById() {
        Employees employee = new Employees();
        employee.setFirstName("Jane");
        employee.setLastName("Smith");
        employee.setEmail("jane.smith@example.com");
        employee.setPhoneNumber("0987654321");
        employee.setHireDate(LocalDate.now());
        employee.setJobTitle("Manager");
        employee.setSalary(60000.0f);
        employee.setDepartment("HR");

        Employees savedEmployee = employeeService.createEmployee(employee);

        Employees foundEmployee = employeeService.getEmployeeById(savedEmployee.getId());

        assertNotNull(foundEmployee);
        assertEquals("Jane", foundEmployee.getFirstName());
        assertEquals("Smith", foundEmployee.getLastName());
    }

    @Test
    void testDeleteEmployee() {
        Employees employee = new Employees();
        employee.setFirstName("Test");
        employee.setLastName("User");
        employee.setEmail("test@example.com");
        employee.setPhoneNumber("1111111111");
        employee.setHireDate(LocalDate.now());
        employee.setJobTitle("Tester");
        employee.setSalary(40000.0f);
        employee.setDepartment("QA");

        Employees savedEmployee = employeeService.createEmployee(employee);
        int employeeId = savedEmployee.getId();

        Employees deletedEmployee = employeeService.deleteEmployee(employeeId);

        assertNotNull(deletedEmployee);
        assertEquals(employeeId, deletedEmployee.getId());
        assertFalse(employeeRepository.findById(employeeId).isPresent());
    }
}
