package com.example.employee_management_system.integration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.example.employee_management_system.config.TestSecurityConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Disabled("Requires view templates")
    void testDashboardPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @Disabled("Requires view templates")
    void testDashboardPageUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Disabled("Requires view templates")
    void testLeaveRequestsPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/leave-requests"))
                .andExpect(status().isOk());
    }

    @Test
    @Disabled("Requires view templates")
    void testReportsPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/reports"))
                .andExpect(status().isOk());
    }

    @Test
    @Disabled("Requires view templates")
    void testSettingsPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/settings"))
                .andExpect(status().isOk());
    }
}
