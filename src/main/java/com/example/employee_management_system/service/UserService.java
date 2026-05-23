package com.example.employee_management_system.service;

import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.mapper.UserMapper;
import com.example.employee_management_system.model.Authorities;
import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.model.Roles;
import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.repository.AuthorityRepository;
import com.example.employee_management_system.repository.EmployeeRepository;
import com.example.employee_management_system.repository.RoleRepository;
import com.example.employee_management_system.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing user operations and their relationship to employees.
 * 
 * <p>This service provides comprehensive user management functionality including:
 * <ul>
 *   <li>Creating users with role and authority assignments</li>
 *   <li>Creating users with temporary passwords for employee onboarding</li>
 *   <li>CRUD operations for user accounts</li>
 *   <li>Role and authority management</li>
 *   <li>CSV import and export functionality</li>
 *   <li>Partial updates via reflection-based patching</li>
 * </ul>
 * </p>
 * 
 * <p>The service integrates with Spring Security for password encoding and
 * uses caching to optimize performance for frequently accessed user data.</p>
 * 
 * <p>All write operations are transactional to ensure data consistency across
 * the user, role, and authority entities.</p>
 * 
 * @author Diaa Eldin
 * @version 1.0.0
 * @since 2025-12-02
 * @see org.springframework.stereotype.Service
 * @see org.springframework.security.crypto.password.PasswordEncoder
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final CacheManager cacheManager;

    /**
     * Constructs a new UserService with the required dependencies.
     * 
     * @param ur the user repository for data access
     * @param er the employee repository for linking users to employees
     * @param roleRepository the role repository for role management
     * @param authorityRepository the authority repository for permission management
     * @param passwordEncoder the password encoder for secure password hashing
     */
    public UserService(UserRepository ur, EmployeeRepository er, RoleRepository roleRepository,
                       AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder, CacheManager cacheManager) {
        this.userRepository = ur;
        this.employeeRepository = er;
        this.roleRepository = roleRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.cacheManager = cacheManager;
    }
    /**
     * Creates a new user account linked to an existing employee.
     * 
     * <p>The password is automatically encoded using the configured PasswordEncoder.</p>
     * 
     * @param id the employee ID to link the user to
     * @param user the user entity to create
     * @return the saved user entity with encoded password
     * @throws RuntimeException if the employee is not found
     * @see org.springframework.security.crypto.password.PasswordEncoder
     */
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public Users createUserForEmployee(int id, Users user) {
        Employees employees = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("employee not found"));
        user.setEmployees(employees);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public Users createUserWithTemporaryPassword(int employeeId, String username, String temporaryPassword, Set<String> roleNames, Set<String> authorityNames) {
        Employees employees = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if (userRepository.findByUserName(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        Users user = new Users();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setEnabled(true);
        user.setTemporaryPassword(true);
        user.setEmailVerified(false);
        user.setEmployees(employees);

        Set<Roles> roles = new HashSet<>();
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                Roles role = getOrCreateRole(roleName);
                // Add authorities to the role
                if (authorityNames != null && !authorityNames.isEmpty()) {
                    Set<Authorities> authorities = new HashSet<>();
                    for (String authorityName : authorityNames) {
                        authorities.add(getOrCreateAuthority(authorityName));
                    }
                    role.setAuthorities(authorities);
                }
                roles.add(role);
            }
        } else {
            Roles role = getOrCreateRole("EMPLOYEE");
            if (authorityNames != null && !authorityNames.isEmpty()) {
                Set<Authorities> authorities = new HashSet<>();
                for (String authorityName : authorityNames) {
                    authorities.add(getOrCreateAuthority(authorityName));
                }
                role.setAuthorities(authorities);
            }
            roles.add(role);
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public Users createUserWithoutEmployee(String username, String temporaryPassword, Set<String> roleNames, Set<String> authorityNames) {
        if (userRepository.findByUserName(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        Users user = new Users();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setEnabled(true);
        user.setTemporaryPassword(true);
        user.setEmailVerified(false);

        Set<Roles> roles = new HashSet<>();
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                Roles role = getOrCreateRole(roleName);
                // Add authorities to the role
                if (authorityNames != null && !authorityNames.isEmpty()) {
                    Set<Authorities> authorities = new HashSet<>();
                    for (String authorityName : authorityNames) {
                        authorities.add(getOrCreateAuthority(authorityName));
                    }
                    role.setAuthorities(authorities);
                }
                roles.add(role);
            }
        } else {
            Roles role = getOrCreateRole("EMPLOYEE");
            if (authorityNames != null && !authorityNames.isEmpty()) {
                Set<Authorities> authorities = new HashSet<>();
                for (String authorityName : authorityNames) {
                    authorities.add(getOrCreateAuthority(authorityName));
                }
                role.setAuthorities(authorities);
            }
            roles.add(role);
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Cacheable(value = "roles", key = "#roleName")
    private Roles getOrCreateRole(String roleName) {
        return roleRepository.findByRole(roleName)
                .orElseGet(() -> {
                    Roles newRole = new Roles();
                    newRole.setRole(roleName);
                    return roleRepository.save(newRole);
                });
    }

    @Cacheable(value = "authorities", key = "#authorityName")
    private Authorities getOrCreateAuthority(String authorityName) {
        return authorityRepository.findByName(authorityName)
                .orElseGet(() -> {
                    Authorities newAuthority = new Authorities();
                    newAuthority.setName(authorityName);
                    return authorityRepository.save(newAuthority);
                });
    }

    @Transactional
    @CacheEvict(value = "users", key = "#username")
    public void updateUserEmail(String username, String email) {
        Users user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        user.getEmployees().setEmail(email);
        userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public Users updateUser(int id, Users updatedUser) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        UserMapper.INSTANCE.updateEntityFromDto(UserMapper.INSTANCE.toDto(updatedUser), user);
        return userRepository.save(user);
    }
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public Users patchUser(int id, Map<String, Object> updates) {
        Users existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        updates.forEach((key, value) -> {
            if (key.equals("id") || key.equals("password")) {
                return; // Don't allow updating ID or password through patch
            }
            
            try {
                Field field = ReflectionUtils.findField(Users.class, key);
                if (field != null) {
                    field.setAccessible(true);
                    
                    // Handle type conversion
                    Object convertedValue = convertValue(value, field.getType());
                    ReflectionUtils.setField(field, existingUser, convertedValue);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid field or value for field: " + key, e);
            }
        });

        return userRepository.save(existingUser);
    }
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(int id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.deleteById(id);
    }
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public Users getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "paginated-users", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public Page<Users> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public Users updateUserRoles(int userId, Set<String> roleNames) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Set<Roles> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByRole(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Transactional
    public void completeAccount(String username, String email, String newPassword) {
        Users user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // Check if email is already in use by another user or employee (excluding current user)
        userRepository.findByEmployeeEmail(email).ifPresent(existingUser -> {
            if (existingUser.getId() != user.getId()) {
                throw new com.example.employee_management_system.exception.UserAlreadyExistsException("Email already in use. Please use a different email.");
            }
        });

        user.getEmployees().setEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTemporaryPassword(false);
        user.setEmailVerified(true);

        userRepository.save(user);

        // Clear all user caches
        clearUserCaches(username);
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        Users user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTemporaryPassword(false);
        user.setEmailVerified(true);

        userRepository.save(user);

        // Clear all user caches
        clearUserCaches(username);
    }

    private void clearUserCaches(String username) {
        String[] cacheNames = {"user-entities-by-username", "users", "users-by-username", "user-entities-by-email"};
        for (String cacheName : cacheNames) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(username);
            }
        }
    }

    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        
        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }
        
        // Handle common type conversions
        if (targetType == String.class) {
            return value.toString();
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Boolean) {
                return value;
            }
            return Boolean.parseBoolean(value.toString());
        }
        
        throw new IllegalArgumentException("Cannot convert " + value + " to " + targetType);
    }

    @Transactional
    public List<Users> uploadUsersFromCSV(MultipartFile file) {
        List<Users> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 3) {
                    Users user = new Users();
                    user.setUserName(values[0].trim());
                    user.setPassword(passwordEncoder.encode(values[1].trim()));
                    user.setEnabled(Boolean.parseBoolean(values[2].trim()));
                    if (values.length > 4 && !values[4].trim().isEmpty()) {
                        user.setTemporaryPassword(Boolean.parseBoolean(values[4].trim()));
                    }
                    if (values.length > 5 && !values[5].trim().isEmpty()) {
                        user.setEmailVerified(Boolean.parseBoolean(values[5].trim()));
                    }
                    users.add(user);
                }
            }
            return userRepository.saveAll(users);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] exportUsersToCSV() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(outputStream)) {
            pw.println("username,password,enabled,isTemporaryPassword,isEmailVerified");
            List<Users> users = userRepository.findAll();
            for (Users user : users) {
                pw.println(String.format("%s,%s,%s,%s,%s",
                    user.getUserName(),
                    "[HIDDEN]",
                    user.isEnabled(),
                    user.isTemporaryPassword(),
                    user.isEmailVerified()
                ));
            }
        }
        return outputStream.toByteArray();
    }
}
