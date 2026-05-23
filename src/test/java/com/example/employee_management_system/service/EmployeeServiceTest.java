package com.example.employee_management_system.service;

import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmployeeService employeeService;

    private Employees testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employees();
        testEmployee.setId(1);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john.doe@example.com");
        testEmployee.setPhoneNumber("1234567890");
        testEmployee.setHireDate(LocalDate.now());
        testEmployee.setJobTitle("Developer");
        testEmployee.setSalary(50000.0f);
        testEmployee.setDepartment("IT");
    }

    @Test
    void testCreateEmployee() {
        when(employeeRepository.save(any(Employees.class))).thenReturn(testEmployee);

        Employees result = employeeService.createEmployee(testEmployee);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(employeeRepository, times(1)).save(any(Employees.class));
    }

    @Test
    void testGetAllEmployees() {
        List<Employees> employees = Arrays.asList(testEmployee);
        Page<Employees> page = new PageImpl<>(employees);
        Pageable pageable = PageRequest.of(0, 10);

        when(employeeRepository.findAll(pageable)).thenReturn(page);

        Page<Employees> result = employeeService.getAllEmployee(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetEmployeeById_Success() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));

        Employees result = employeeService.getEmployeeById(1);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(employeeRepository, times(1)).findById(1);
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(999));
        verify(employeeRepository, times(1)).findById(999);
    }

    @Test
    void testDeleteEmployee_Success() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        doNothing().when(employeeRepository).deleteById(1);

        Employees result = employeeService.deleteEmployee(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(employeeRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteEmployee_NotFound() {
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(999));
        verify(employeeRepository, never()).deleteById(anyInt());
    }

    @Test
    void testUpdateEmployee_Success() {
        Employees updatedEmployee = new Employees();
        updatedEmployee.setFirstName("Jane");
        updatedEmployee.setLastName("Smith");

        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employees.class))).thenReturn(testEmployee);

        Employees result = employeeService.updateEmployee(1, updatedEmployee);

        assertNotNull(result);
        verify(employeeRepository, times(1)).save(any(Employees.class));
    }

    @Test
    void testUpdateEmployee_NotFound() {
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        Employees updatedEmployee = new Employees();
        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(999, updatedEmployee));
        verify(employeeRepository, never()).save(any(Employees.class));
    }
}
