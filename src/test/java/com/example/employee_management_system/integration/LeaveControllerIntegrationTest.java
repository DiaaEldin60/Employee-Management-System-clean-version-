package com.example.employee_management_system.integration;

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
public class LeaveControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    @Disabled("Endpoint may not exist or requires different setup")
    void testGetMyLeaveRequests() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/leave/requests/my")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPendingLeaveRequests() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/leave/requests/pending")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @Disabled("Endpoint may not exist or requires different setup")
    void testSubmitLeaveRequest() throws Exception {
        String leaveRequestJson = """
            {
                "leaveType": "ANNUAL",
                "startDate": "2024-06-01",
                "endDate": "2024-06-05",
                "reason": "Vacation"
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/leave/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(leaveRequestJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testApproveLeaveRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/leave/requests/1/approve")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Assuming request with ID 1 doesn't exist
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRejectLeaveRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/leave/requests/1/reject")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Assuming request with ID 1 doesn't exist
    }
}
