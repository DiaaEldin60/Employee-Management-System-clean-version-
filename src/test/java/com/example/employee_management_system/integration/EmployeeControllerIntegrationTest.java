package com.example.employee_management_system.integration;

import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.repository.EmployeeRepository;
import com.example.employee_management_system.repository.LeaveBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import com.example.employee_management_system.config.TestSecurityConfig;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
    @WithMockUser(roles = "ADMIN")
    void testGetAllEmployees() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Disabled("Requires valid user association")
    void testCreateEmployee() throws Exception {
        String employeeJson = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "email": "john.doe@example.com",
                "phoneNumber": "1234567890",
                "hireDate": "2024-01-01",
                "jobTitle": "Developer",
                "salary": 50000.0,
                "department": "IT"
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @Disabled("Requires user with associated employee record")
    void testGetEmployeeById() throws Exception {
        Employees employee = new Employees();
        employee.setFirstName("Test");
        employee.setLastName("User");
        employee.setEmail("test@example.com");
        employee.setHireDate(java.time.LocalDate.now());
        employee = employeeRepository.save(employee);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/" + employee.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
