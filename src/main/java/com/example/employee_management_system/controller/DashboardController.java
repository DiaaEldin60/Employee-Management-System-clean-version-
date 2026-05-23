package com.example.employee_management_system.controller;

import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.repository.EmployeeRepository;
import com.example.employee_management_system.repository.EmailVerificationTokenRepository;
import com.example.employee_management_system.repository.UserRepository;
import com.example.employee_management_system.securityConfig.CustomUserDetailsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final CustomUserDetailsService userDetailsService;

    public DashboardController(EmployeeRepository employeeRepository,
                               UserRepository userRepository,
                               EmailVerificationTokenRepository emailVerificationTokenRepository,
                               CustomUserDetailsService userDetailsService) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/dashboard")
    /// BY ME
    /// Model is used to pass data from the controller to the view
    public String dashboard(Model model) {
        // Fetch real statistics from database
        long totalEmployees = employeeRepository.count();
        long totalUsers = userRepository.count();
        long pendingVerifications = emailVerificationTokenRepository.count();
        long activeUsers = userRepository.countByEnabled(true);
        long departments = employeeRepository.countDistinctDepartments();

        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("pendingVerifications", pendingVerifications);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("departments", departments);

        // Fetch department distribution data
        List<Object[]> departmentData = employeeRepository.countEmployeesByDepartment();
        model.addAttribute("departmentData", departmentData);

        return "dashboard";
    }

    @GetMapping("/employees")
    public String employees() {
        return "employees";
    }

    @GetMapping("/leave-requests")
    public String leaveRequests() {
        return "leave-requests";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("totalEmployees", employeeRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        return "reports";
    }

    @GetMapping("/settings")
    public String settings(Model model, Authentication authentication) {
        String username = authentication.getName();
        Users user = userDetailsService.getUserByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("email", user.getEmployees() != null ? user.getEmployees().getEmail() : "");
        model.addAttribute("roles", user.getRoles());
        model.addAttribute("firstName", user.getEmployees() != null ? user.getEmployees().getFirstName() : "");
        model.addAttribute("lastName", user.getEmployees() != null ? user.getEmployees().getLastName() : "");
        model.addAttribute("phone", user.getEmployees() != null ? user.getEmployees().getPhoneNumber() : "");

        return "settings";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        return settings(model, authentication);
    }
}
