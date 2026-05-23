package com.example.employee_management_system.controller;

import com.example.employee_management_system.dto.EmployeeDto;
import com.example.employee_management_system.dto.EmployeePatchDto;
import com.example.employee_management_system.dto.EmployeeWithUserCreationDto;
import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.mapper.EmployeeMapper;
import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.repository.EmployeeRepository;
import com.example.employee_management_system.securityConfig.CustomUserDetailsService;
import com.example.employee_management_system.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for managing employee operations.
 * 
 * <p>This controller provides endpoints for CRUD operations on employees, including
 * advanced features like CSV import/export and partial updates via PATCH.</p>
 * 
 * <p>All endpoints require authentication, and access is controlled by role with record-level filtering:</p>
 * <ul>
 *   <li>ADMIN - Full access to all operations (create, read, update, delete) on all employees</li>
 *   <li>MANAGER - Read access to employees in their own department only</li>
 *   <li>EMPLOYEE - Read-only access to their own employee record only</li>
 * </ul>
 * 
 * <p>The controller uses Spring's {@link ResponseEntity} to provide full control over
 * HTTP status codes, headers, and response bodies.</p>
 * 
 * @author Diaa Eldin
 * @version 1.0.0
 * @since 2025-12-02
 * @see EmployeeService
 * @see RestController
 */
@RestController
@RequestMapping("/api/v1/employees")
/// by me
/// organize and group API endpoints in the generated API documentation UI.
@Tag(name = "Employee Management", description = "Employee management APIs")
public class EmployeeController {

    /**
     * Service for employee business logic.
     */
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructs a new EmployeeController with the required service.
     * 
     * @param employeeService the employee service for business operations
     * @param employeeRepository the employee repository for data access
     * @param userDetailsService the user details service for retrieving current user
     */
    public EmployeeController(EmployeeService employeeService,
                              EmployeeRepository employeeRepository,
                              CustomUserDetailsService userDetailsService) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Gets employees based on user's role:
     * - EMPLOYEE: only their own record
     * - MANAGER: employees in their department
     * - ADMIN: all employees
     */
    /// by me
    /// Authentication is used to retrieve the currently authenticated user/request.
    private List<Employees> getEmployeesForCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        Users user = userDetailsService.getUserByUsername(username);
        
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isManager = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
        
        if (isAdmin) {
            return employeeRepository.findAll();
        }
        
        if (isManager && user.getEmployees() != null) {
            String department = user.getEmployees().getDepartment();
            if (department != null) {
                return employeeRepository.findByDepartment(department);
            }
        }
        
        // EMPLOYEE or fallback - return only their own record
        if (user.getEmployees() != null) {
            return Collections.singletonList(user.getEmployees());
        }
        
        return Collections.emptyList();
    }

    /**
     * Checks if user can access a specific employee record.
     */
    private boolean canAccessEmployee(Authentication authentication, Integer employeeId) {
        String username = authentication.getName();
        Users user = userDetailsService.getUserByUsername(username);
        
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            return true;
        }
        
        Employees targetEmployee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        boolean isManager = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
        
        if (isManager && user.getEmployees() != null && targetEmployee.getDepartment() != null) {
            String userDept = user.getEmployees().getDepartment();
            String targetDept = targetEmployee.getDepartment();
            if (userDept != null && userDept.equals(targetDept)) {
                return true;
            }
        }
        
        // EMPLOYEE - can only access their own record
        if (user.getEmployees() != null && user.getEmployees().getId() == employeeId) {
            return true;
        }
        
        return false;
    }

    /**
     * Retrieves all employees with optional filtering and pagination.
     * 
     * <p>Supports filtering by first name, last name, email, department, job title,
     * salary range, and hire date range. Results are paginated and can be sorted.</p>
     * 
     * @param page page number (0-indexed), defaults to 0
     * @param size number of items per page (max 20), defaults to 10
     * @param sortBy field to sort by, defaults to "id"
     * @return paginated list of employees matching the criteria
     */
    ///  by me
    /// these annotations are used to document the API and provide metadata for the controller.
    @Operation(summary = "Get all employees with pagination and filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    /// by me
    /// this annotation is used to enforce role-based access control.
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) Float minSalary,
            @RequestParam(required = false) Float maxSalary,
            @RequestParam(required = false) String hireDateFrom,
            @RequestParam(required = false) String hireDateTo,
            Authentication authentication)
    {
        /// by me
        /// Pageable is used to specify the pagination parameters.
        /// Page interface is used to represent a page of results.
        Pageable pageable = PageRequest.of(page, Math.min(size, 20), Sort.by(sortBy));
        
        // Check if any filter parameters are provided
        boolean hasFilters = firstName != null || lastName != null || email != null || 
                           department != null || jobTitle != null || phoneNumber != null || 
                           minSalary != null || maxSalary != null || hireDateFrom != null || hireDateTo != null;
        
        Page<Employees> employeesPage;
        /// by me
        /// fallback to regular pagination if no filters are provided
        /// filtering by date range
        if (hasFilters) {
            // Use filtering service - show only filtered entities
            LocalDate fromDate = hireDateFrom != null ? LocalDate.parse(hireDateFrom) : null;
            LocalDate toDate = hireDateTo != null ? LocalDate.parse(hireDateTo) : null;
            
            employeesPage = employeeService.filterEmployees(
                firstName, lastName, email, department, jobTitle, phoneNumber,
                minSalary, maxSalary, fromDate, toDate, pageable);
        } else {
            // Use regular pagination with role-based access
            List<Employees> employees = getEmployeesForCurrentUser(authentication);
            
            // Convert to page
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), employees.size());
            List<Employees> pagedEmployees = employees.subList(start, end);
            
            employeesPage = new PageImpl<>(pagedEmployees, pageable, employees.size());
        }
        
        // Convert to DTOs
        Page<EmployeeDto> employeeDtos = employeesPage.map(EmployeeMapper.INSTANCE::toDto);
        return ResponseEntity.ok(employeeDtos);
    }
    @Operation(summary = "Create a new employee with user credentials")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Employee and user created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    /// by me
    /// ResponseEntity is used to return a response with a status code and body.
    /// @Valid is used to validate the request body.
    /// @RequestBody is used to bind the request body to the EmployeeWithUserCreationDto.
    /// @PathVariable is used to bind the path variable to the id.
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeWithUserCreationDto employeeWithUserDto)
    {
        Employees saved = employeeService.createEmployeeWithUser(employeeWithUserDto);
        EmployeeDto savedDto = EmployeeMapper.INSTANCE.toDto(saved);
        /// by me
        /// URI is used to represent a URL.
        /// ServletUriComponentsBuilder is used to build a URI.
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedDto);
    }
    @Operation(summary = "Get employee by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Employee not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable int id, Authentication authentication)
    {
        // Check if user can access this employee
        if (!canAccessEmployee(authentication, id)) {
            return ResponseEntity.status(403).build();
        }
        
        Employees employee = employeeService.getEmployeeById(id);
        EmployeeDto employeeDto = EmployeeMapper.INSTANCE.toDto(employee);
        return ResponseEntity.ok(employeeDto);
    }
    @Operation(summary = "Update employee completely")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Employee not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable int id, @Valid @RequestBody EmployeeDto employeeDto)
    {
        Employees updated = employeeService.updateEmployee(id, employeeDto);
        EmployeeDto updatedDto = EmployeeMapper.INSTANCE.toDto(updated);
        return ResponseEntity.ok(updatedDto);
    }
    @Operation(summary = "Partially update employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
        @ApiResponse(responseCode = "404", description = "Employee not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDto> patchUpdateEmployee(
            @PathVariable int id, @RequestBody EmployeePatchDto employeePatchDto)
    {
        Employees updated = employeeService.patchEmployee(id, employeePatchDto);
        EmployeeDto updatedDto = EmployeeMapper.INSTANCE.toDto(updated);
        return ResponseEntity.ok(updatedDto);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int id)
    {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Upload employees from CSV file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employees uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid CSV format"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadEmployees(@RequestParam("file") MultipartFile file)
    {
        List<Employees> uploaded = employeeService.uploadEmployeesFromCSV(file);
        return ResponseEntity.ok(Map.of(
            "message", "Employees uploaded successfully",
            "count", uploaded.size(),
            "employees", uploaded
        ));
    }

    @Operation(summary = "Validate employees from CSV file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CSV validated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid CSV format"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/validate")
    /// by me
    /// MultipartFile is used to receive the CSV file
    public ResponseEntity<Map<String, Object>> validateEmployees(@RequestParam("file") MultipartFile file)
    {
        List<Map<String, Object>> validated = employeeService.validateEmployeesFromCSV(file);
        return ResponseEntity.ok(Map.of(
            "message", "CSV validated successfully",
            "count", validated.size(),
            "employees", validated
        ));
    }

    @Operation(summary = "Export employees to CSV")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CSV file generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportEmployees(Authentication authentication)
    {
        // Get employees based on user's role
        List<Employees> employees = getEmployeesForCurrentUser(authentication);
        byte[] csvData = employeeService.exportEmployeesToCSV(employees);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=employees.csv")
            .header("Content-Type", "text/csv")
            .body(csvData);
    }
}
