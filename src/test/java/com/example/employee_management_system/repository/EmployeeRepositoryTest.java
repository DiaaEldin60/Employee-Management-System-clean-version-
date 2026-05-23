package com.example.employee_management_system.repository;

import com.example.employee_management_system.model.Employees;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@ActiveProfiles("test")
public class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employees testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employees();
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
    void testSaveEmployee() {
        Employees saved = employeeRepository.save(testEmployee);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("John", saved.getFirstName());
    }

    @Test
    void testFindById_Success() {
        Employees saved = entityManager.persist(testEmployee);

        Employees found = employeeRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals("John", found.getFirstName());
    }

    @Test
    void testFindById_NotFound() {
        var result = employeeRepository.findById(999);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAll() {
        entityManager.persist(testEmployee);

        var employees = employeeRepository.findAll();

        assertFalse(employees.isEmpty());
        assertEquals(1, employees.size());
    }

    @Test
    void testDeleteById() {
        Employees saved = entityManager.persist(testEmployee);

        employeeRepository.deleteById(saved.getId());

        var result = employeeRepository.findById(saved.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void testPagination() {
        entityManager.persist(testEmployee);

        Page<Employees> page = employeeRepository.findAll(PageRequest.of(0, 10));

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
    }
}
