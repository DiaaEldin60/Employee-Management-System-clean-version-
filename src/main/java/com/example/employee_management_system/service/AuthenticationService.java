package com.example.employee_management_system.service;

import com.example.employee_management_system.exception.AuthenticationException;
import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.exception.UserAlreadyExistsException;
import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.model.Roles;
import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.repository.RoleRepository;
import com.example.employee_management_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//handles registration and login logic.

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final EmployeeService employeeService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthenticationService(UserRepository userRepository, EmployeeService employeeService, UserService userService, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.employeeService = employeeService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }
    /*
    Option A (employee-driven): The employee already exists in the employees table → you select the employee_id and connect it to the new User.
    Option B (user-driven): Registration creates both Employee and User in the same step.
    * */
    //this implements option A
    @Transactional
    public void registerEmployeeToUser(String userName, String password, int id) {
        // Check if user already exists
        if (userRepository.findByUserName(userName).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists: " + userName);
        }

        Employees employees = employeeService.getEmployeeById(id);
        // Fetch existing EMPLOYEE role from database
        Roles employeeRole = roleRepository.findByRole("EMPLOYEE")
                .orElseThrow(() -> new ResourceNotFoundException("EMPLOYEE role not found in database"));

        // Create user with disabled account until email verification
        Users user = new Users(userName, password, false, employees);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(java.util.Set.of(employeeRole));
        userRepository.save(user);
    }

    public String login(String username, String email, String password) {
        if (username == null && email == null) {
            throw new AuthenticationException("Username or email is required");
        }

        Users user;
        if (username != null) {
            user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
        } else {
            user = userRepository.findByEmployeeEmail(email)
                    .orElseThrow(() -> new AuthenticationException("Invalid email or password"));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        if (!user.isEnabled()) {
            throw new AuthenticationException("User account is disabled");
        }

        String identifier = username != null ? username : email;
        return "Login successful for user: " + identifier;
    }
}
