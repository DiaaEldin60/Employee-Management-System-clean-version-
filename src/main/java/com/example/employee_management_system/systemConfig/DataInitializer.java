package com.example.employee_management_system.systemConfig;

import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.model.LeaveBalance;
import com.example.employee_management_system.model.Roles;
import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.repository.EmployeeRepository;
import com.example.employee_management_system.repository.LeaveBalanceRepository;
import com.example.employee_management_system.repository.RoleRepository;
import com.example.employee_management_system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final LeaveBalanceRepository leaveBalanceRepository;

    public DataInitializer(UserRepository userRepository, EmployeeRepository employeeRepository,
                         RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                         LeaveBalanceRepository leaveBalanceRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.leaveBalanceRepository = leaveBalanceRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUserName("admin").isEmpty()) {
            // Create admin user first (user_id is required by schema)
            Users admin = new Users();
            admin.setUserName("admin");
            admin.setPassword(passwordEncoder.encode("change_me"));
            admin.setEnabled(true);
            admin.setTemporaryPassword(false);
            admin.setEmailVerified(true);

            // Set roles properly - fetch existing or create new
            Set<Roles> roles = new HashSet<>();
            roles.add(getOrCreateRole("ADMIN"));
            roles.add(getOrCreateRole("MANAGER"));
            roles.add(getOrCreateRole("EMPLOYEE"));
            admin.setRoles(roles);

            Users savedAdmin = userRepository.save(admin);

            // Now create employee linked to the saved user
            Employees adminEmployee = employeeRepository.findByEmail("admin@example.com")
                    .orElse(null);

            if (adminEmployee == null) {
                adminEmployee = new Employees();
                adminEmployee.setFirstName("Admin");
                adminEmployee.setLastName("User");
                adminEmployee.setEmail("admin@example.com");
                adminEmployee.setPhoneNumber("1234567890");
                adminEmployee.setHireDate(LocalDate.now());
                adminEmployee.setJobTitle("System Administrator");
                adminEmployee.setSalary(75000.0f);
                adminEmployee.setDepartment("IT");
                adminEmployee.setUser(savedAdmin);  // Set the user_id
                adminEmployee = employeeRepository.save(adminEmployee);
            }

            // Update the user with the employee reference
            savedAdmin.setEmployees(adminEmployee);
            userRepository.save(savedAdmin);
        }

        // Create mock employees and users together
        createMockEmployeeWithUser("John", "Doe", "john.doe@example.com", "555-0101", "Software Engineer", "IT", 65000.0f, "jdoe", "change_me", Set.of("EMPLOYEE"));
        createMockEmployeeWithUser("Jane", "Smith", "jane.smith@example.com", "555-0102", "Project Manager", "IT", 85000.0f, "jsmith", "change_me", Set.of("MANAGER", "EMPLOYEE"));
        createMockEmployeeWithUser("Bob", "Johnson", "bob.johnson@example.com", "555-0103", "Data Analyst", "Finance", 60000.0f, "bjohnson", "change_me", Set.of("EMPLOYEE"));
        createMockEmployeeWithUser("Alice", "Williams", "alice.williams@example.com", "555-0104", "HR Manager", "Human Resources", 70000.0f, "awilliams", "change_me", Set.of("MANAGER", "EMPLOYEE"));
        createMockEmployeeWithUser("Charlie", "Brown", "charlie.brown@example.com", "555-0105", "Sales Representative", "Sales", 55000.0f, "cbrown", "change_me", Set.of("EMPLOYEE"));
        createMockEmployeeWithUser("Diana", "Miller", "diana.miller@example.com", "555-0106", "Marketing Specialist", "Marketing", 58000.0f, "dmiller", "change_me", Set.of("EMPLOYEE"));
        createMockEmployeeWithUser("Edward", "Davis", "edward.davis@example.com", "555-0107", "Software Developer", "IT", 68000.0f, "edavis", "change_me", Set.of("EMPLOYEE"));
        createMockEmployeeWithUser("Fiona", "Garcia", "fiona.garcia@example.com", "555-0108", "UX Designer", "IT", 62000.0f, "fgarcia", "change_me", Set.of("EMPLOYEE"));
        createMockEmployeeWithUser("George", "Martinez", "george.martinez@example.com", "555-0109", "Accountant", "Finance", 59000.0f, "gmartinez", "change_me", Set.of("EMPLOYEE"));
        createMockEmployeeWithUser("Hannah", "Anderson", "hannah.anderson@example.com", "555-0110", "Recruiter", "Human Resources", 55000.0f, "handerson", "change_me", Set.of("EMPLOYEE"));

        // Initialize leave balances for all employees that don't have one
        employeeRepository.findAll().forEach(employee -> {
            if (!leaveBalanceRepository.existsByEmployee(employee)) {
                LeaveBalance balance = new LeaveBalance(employee);
                leaveBalanceRepository.save(balance);
            }
        });
    }

    private void createMockEmployeeWithUser(String firstName, String lastName, String email, String phone, String jobTitle, String department, float salary, String username, String password, Set<String> roleNames) {
        if (employeeRepository.findByEmail(email).isEmpty()) {
            // Create user first if not exists
            Users savedUser = userRepository.findByUserName(username).orElse(null);
            if (savedUser == null) {
                Users user = new Users();
                user.setUserName(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setEnabled(true);
                user.setTemporaryPassword(false);
                user.setEmailVerified(true);

                Set<Roles> roles = new HashSet<>();
                for (String roleName : roleNames) {
                    roles.add(getOrCreateRole(roleName));
                }
                user.setRoles(roles);
                savedUser = userRepository.save(user);
            }

            // Create employee with user
            Employees employee = new Employees();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setEmail(email);
            employee.setPhoneNumber(phone);
            employee.setHireDate(LocalDate.now());
            employee.setJobTitle(jobTitle);
            employee.setSalary(salary);
            employee.setDepartment(department);
            employee.setUser(savedUser);
            employeeRepository.save(employee);
        }
    }

    private void createMockEmployee(String firstName, String lastName, String email, String phone, String jobTitle, String department, float salary) {
        if (employeeRepository.findByEmail(email).isEmpty()) {
            Employees employee = new Employees();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setEmail(email);
            employee.setPhoneNumber(phone);
            employee.setHireDate(LocalDate.now());
            employee.setJobTitle(jobTitle);
            employee.setSalary(salary);
            employee.setDepartment(department);
            employeeRepository.save(employee);
        }
    }

    private void createMockUser(String username, String password, String email, Set<String> roleNames) {
        if (userRepository.findByUserName(username).isEmpty()) {
            Employees employee = employeeRepository.findByEmail(email).orElse(null);
            if (employee != null) {
                Users user = new Users();
                user.setUserName(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setEnabled(true);
                user.setTemporaryPassword(false);
                user.setEmailVerified(true);
                user.setEmployees(employee);

                Set<Roles> roles = new HashSet<>();
                for (String roleName : roleNames) {
                    roles.add(getOrCreateRole(roleName));
                }
                user.setRoles(roles);

                userRepository.save(user);
            }
        }
    }

    private Roles getOrCreateRole(String roleName) {
        return roleRepository.findByRole(roleName)
                .orElseGet(() -> {
                    Roles newRole = new Roles();
                    newRole.setRole(roleName);
                    return roleRepository.save(newRole);
                });
    }
}
