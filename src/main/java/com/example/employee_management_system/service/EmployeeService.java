package com.example.employee_management_system.service;

import com.example.employee_management_system.dto.EmployeePatchDto;
import com.example.employee_management_system.dto.EmployeeWithUserCreationDto;
import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.mapper.EmployeeMapper;
import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.repository.EmployeeRepository;
import com.example.employee_management_system.specification.EmployeeSpecification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing employee operations.
 * 
 * <p>This service provides comprehensive CRUD operations for employees, including:
 * <ul>
 *   <li>Creating, reading, updating, and deleting employee records</li>
 *   <li>Creating employees with associated user accounts</li>
 *   <li>Partial updates via reflection-based patching</li>
 *   <li>Filtering and searching employees with dynamic criteria</li>
 *   <li>CSV import and export functionality</li>
 * </ul>
 * </p>
 * 
 * <p>The service uses Spring's caching abstraction with Ehcache to improve performance:
 * <ul>
 *   <li>{@link Cacheable} - Caches read operations</li>
 *   <li>{@link CacheEvict} - Clears cache on write operations</li>
 * </ul>
 * </p>
 * 
 * <p>All write operations are marked with {@link Transactional} to ensure data consistency.</p>
 * 
 * @author Diaa Eldin
 * @version 1.0.0
 * @since 2025-12-02
 * @see org.springframework.stereotype.Service
 * @see org.springframework.cache.annotation.Cacheable
 * @see org.springframework.cache.annotation.CacheEvict
 */
@Service
public class EmployeeService {
    private final EmployeeRepository emp;
    private final UserService userService;

    /**
     * Constructs a new EmployeeService with the required dependencies.
     * 
     * @param emp the employee repository for data access
     * @param userService the user service for managing associated user accounts
     */
    public EmployeeService(EmployeeRepository emp, UserService userService) {
        this.emp = emp;
        this.userService = userService;
    }
    /**
     * Creates a new employee record.
     * 
     * <p>This method clears the employees cache to ensure data consistency.</p>
     * 
     * @param employees the employee entity to create
     * @return the saved employee entity with generated ID
     * @see org.springframework.cache.annotation.CacheEvict
     */
    /// by me
    /// CacheEvict annotation is used to clear the cache after any operation
    /// employees is the cache name
    /// allEntries = true means clear the entire cache
    @CacheEvict(value = "employees", allEntries = true)
    public Employees createEmployee(Employees employees) {
        return emp.save(employees);
    }

    /**
     * Creates a new employee with an associated user account.
     * 
     * <p>This method performs the following steps:
     * <ol>
     *   <li>Creates a user account with the provided credentials and roles</li>
     *   <li>Creates an employee record with the user's information</li>
     *   <li>Establishes bidirectional relationship between user and employee</li>
     *   <li>Saves the employee (cascade saves the user)</li>
     * </ol>
     * </p>
     * 
     * <p>The operation is transactional - if any step fails, all changes are rolled back.</p>
     * 
     * @param employeeWithUserDto the DTO containing employee and user creation data
     * @return the created employee entity with associated user
     * @throws com.example.employee_management_system.exception.ResourceNotFoundException if related entities not found
     * @see com.example.employee_management_system.dto.EmployeeWithUserCreationDto
     * @see com.example.employee_management_system.model.Users
     */

    /// by me
    /// Transactional annotation is used to ensure data consistency by rolling back on failure
    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public Employees createEmployeeWithUser(EmployeeWithUserCreationDto employeeWithUserDto) {
        // Create user first without employee
        Users user = userService.createUserWithoutEmployee(
            employeeWithUserDto.getUsername(),
            employeeWithUserDto.getTemporaryPassword(),
            employeeWithUserDto.getRoles(),
            employeeWithUserDto.getAuthorities()
        );

        // Create employee with user
        Employees employee = new Employees();
        employee.setFirstName(employeeWithUserDto.getFirstName());
        employee.setLastName(employeeWithUserDto.getLastName());
        employee.setEmail(employeeWithUserDto.getEmail() != null ? employeeWithUserDto.getEmail() : "");
        employee.setPhoneNumber(employeeWithUserDto.getPhoneNumber());
        employee.setHireDate(employeeWithUserDto.getHireDate());
        employee.setJobTitle(employeeWithUserDto.getJobTitle());
        employee.setSalary(employeeWithUserDto.getSalary());
        employee.setDepartment(employeeWithUserDto.getDepartment());
        employee.setUser(user);
        user.setEmployees(employee); // Set bidirectional relationship

        return emp.save(employee);
    }
    /**
     * Updates an existing employee record with new data.
     * 
     * <p>Uses MapStruct to map DTO fields to the existing entity, preserving
     * any fields not included in the update. The cache is evicted for the
     * specific employee ID to maintain consistency.</p>
     * 
     * @param id the unique identifier of the employee to update
     * @param employeeDto the DTO containing updated employee data
     * @return the updated employee entity
     * @throws com.example.employee_management_system.exception.ResourceNotFoundException if employee not found
     * @see com.example.employee_management_system.mapper.EmployeeMapper
     */
    @Transactional
    @CacheEvict(value = "employees", key = "#id")
    public Employees updateEmployee(int id, com.example.employee_management_system.dto.EmployeeDto employeeDto) {
        Employees existingEmployee = emp.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        EmployeeMapper.INSTANCE.updateEntityFromDto(employeeDto, existingEmployee);
        return emp.save(existingEmployee);
    }

    /**
     * Updates an existing employee record with new data.
     * 
     * <p>Uses MapStruct to map DTO fields to the existing entity, preserving
     * any fields not included in the update. The cache is evicted for the
     * specific employee ID to maintain consistency.</p>
     * 
     * @param id the unique identifier of the employee to update
     * @param employees the employee entity containing updated data
     * @return the updated employee entity
     * @throws com.example.employee_management_system.exception.ResourceNotFoundException if employee not found
     * @see com.example.employee_management_system.mapper.EmployeeMapper
     */
    @Transactional
    @CacheEvict(value = "employees", key = "#id")
    public Employees updateEmployee(int id, Employees employees) {
        Employees existingEmployee = emp.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        EmployeeMapper.INSTANCE.updateEntityFromDto(EmployeeMapper.INSTANCE.toDto(employees), existingEmployee);
        return emp.save(existingEmployee);
    }
    /**
     * Partially updates an existing employee record using a patch DTO.
     * 
     * <p>This method updates only the non-null fields from the patch DTO,
     * preserving existing values for null fields. The cache is evicted for the
     * specific employee ID to maintain consistency.</p>
     * 
     * @param id the unique identifier of the employee to update
     * @param employeePatchDto the DTO containing partial employee data
     * @return the updated employee entity
     * @throws com.example.employee_management_system.exception.ResourceNotFoundException if employee not found
     * @see com.example.employee_management_system.dto.EmployeePatchDto
     */
    @Transactional
    @CacheEvict(value = "employees", key = "#id")
    public Employees patchEmployee(int id, EmployeePatchDto employeePatchDto) {
        Employees existingEmployee = emp.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        EmployeeMapper.INSTANCE.updateEntityFromPatchDto(employeePatchDto, existingEmployee);
        return emp.save(existingEmployee);
    }

    /**
     * Partially updates an existing employee record using a map of field updates.
     * 
     * <p>This reflection-based PATCH logic dynamically finds an entity field at runtime
     * using the request key, bypasses private access restrictions, converts the incoming
     * value to the correct field type, and then updates the existing object field dynamically
     * without hardcoded setters.</p>
     * 
     * @param id the unique identifier of the employee to update
     * @param updates map of field names to values to update
     * @return the updated employee entity
     * @throws com.example.employee_management_system.exception.ResourceNotFoundException if employee not found
     */
    @Transactional
    @CacheEvict(value = "employees", key = "#id")
    public Employees patchEmployee(int id, Map<String, Object> updates) {
        Employees existingEmployee = emp.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));


        /// by me
        /// This reflection-based PATCH logic dynamically finds an entity field at runtime using the request key, bypasses private access restrictions, converts the incoming value to the correct field type, and then updates the existing object field dynamically without hardcoded setters.
        updates.forEach((key, value) -> {
            if (key.equals("id")) {
                return; // Don't allow updating ID
            }
            /// by me
            /// this is used to update the employee fields dynamically in runtime
            /// ReflectionUtils is used to access the private fields of the Employees class
            /// and set the new value
            /// convertValue is used to convert the value to the correct type
            /// Field is used to get the field of the Employees class
            /// ReflectionUtils.findField is used to find the field of the Employees class
            /// ReflectionUtils.setField is used to set the new value
            try {
                Field field = ReflectionUtils.findField(Employees.class, key);
                if (field != null) {
                    field.setAccessible(true);
                    
                    // Handle type conversion
                    Object convertedValue = convertValue(value, field.getType());
                    ReflectionUtils.setField(field, existingEmployee, convertedValue);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid field or value for field: " + key, e);
            }
        });

        return emp.save(existingEmployee);
    }
    @Transactional
    @CacheEvict(value = "employees", key = "#id")
    public Employees deleteEmployee(int id) {
        Employees employee = emp.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        emp.deleteById(id);
        return employee;
    }
    /**
     * Retrieves an employee by their unique identifier.
     * 
     * <p>Result is cached for improved performance on subsequent requests.</p>
     * 
     * @param id the unique identifier of the employee
     * @return the employee entity
     * @throws com.example.employee_management_system.exception.ResourceNotFoundException if employee not found
     * @see org.springframework.cache.annotation.Cacheable
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "employees", key = "#id")
    public Employees getEmployeeById(int id) {
        return emp.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }
    
    /**
     * Retrieves all employees with pagination support.
     * 
     * <p>Results are cached based on pagination parameters for optimal performance.</p>
     * 
     * @param pageable the pagination information (page number, size, sorting)
     * @return a page of employee entities
     * @see org.springframework.data.domain.Page
     * @see org.springframework.data.domain.Pageable
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "paginated-employees", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public Page<Employees> getAllEmployee(Pageable pageable) {
        return emp.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "paginated-employees", key = "#firstName + '-' + #lastName + '-' + #email + '-' + #department + '-' + #jobTitle + '-' + #phoneNumber + '-' + #minSalary + '-' + #maxSalary + '-' + #hireDateFrom + '-' + #hireDateTo + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public Page<Employees> filterEmployees(
            String firstName,
            String lastName,
            String email,
            String department,
            String jobTitle,
            String phoneNumber,
            Float minSalary,
            Float maxSalary,
            LocalDate hireDateFrom,
            LocalDate hireDateTo,
            Pageable pageable) {
        
        Specification<Employees> spec = EmployeeSpecification.buildSpecification(
                firstName, lastName, email, department, jobTitle, phoneNumber,
                minSalary, maxSalary, hireDateFrom, hireDateTo);
        
        return emp.findAll(spec, pageable);
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
        } else if (targetType == Float.class || targetType == float.class) {
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            }
            return Float.parseFloat(value.toString());
        } else if (targetType == Integer.class || targetType == int.class) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Boolean) {
                return value;
            }
            return Boolean.parseBoolean(value.toString());
        }
        
        throw new IllegalArgumentException("Cannot convert " + value + " to " + targetType);
    }

    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public List<Employees> uploadEmployeesFromCSV(MultipartFile file) {
        List<Employees> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                /// BY ME
                /// Check if the line has at least 7 columns
                if (values.length >= 7) {
                    Employees employee = new Employees();
                    employee.setFirstName(values[0].trim());
                    employee.setLastName(values[1].trim());
                    employee.setEmail(values[2].trim());
                    employee.setDepartment(values[3].trim());
                    employee.setJobTitle(values[4].trim());
                    employee.setPhoneNumber(values[5].trim());
                    employee.setSalary(Float.parseFloat(values[6].trim()));
                    if (values.length > 7 && !values[7].trim().isEmpty()) {
                        employee.setHireDate(LocalDate.parse(values[7].trim()));
                    }
                    employees.add(employee);
                }
            }
            return emp.saveAll(employees);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> validateEmployeesFromCSV(MultipartFile file) {
        List<Map<String, Object>> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean headerSkipped = false;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 7) {
                    Map<String, Object> employee = new HashMap<>();
                    employee.put("lineNumber", lineNumber);
                    employee.put("firstName", values[0].trim());
                    employee.put("lastName", values[1].trim());
                    employee.put("email", values[2].trim());
                    employee.put("department", values[3].trim());
                    employee.put("jobTitle", values[4].trim());
                    employee.put("phoneNumber", values[5].trim());
                    try {
                        employee.put("salary", Float.parseFloat(values[6].trim()));
                        employee.put("valid", true);
                    } catch (NumberFormatException e) {
                        employee.put("salary", values[6].trim());
                        employee.put("valid", false);
                        employee.put("error", "Invalid salary format");
                    }
                    if (values.length > 7 && !values[7].trim().isEmpty()) {
                        try {
                            employee.put("hireDate", values[7].trim());
                            employee.put("valid", employee.get("valid"));
                        } catch (Exception e) {
                            employee.put("hireDate", values[7].trim());
                            employee.put("valid", false);
                            employee.put("error", "Invalid date format");
                        }
                    } else {
                        employee.put("hireDate", "");
                    }
                    employees.add(employee);
                }
            }
            return employees;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }
    }

    /**
     * Exports all employees to CSV format.
     * 
     * <p>Generates a CSV file with the following columns:
     * firstName, lastName, email, department, jobTitle, phoneNumber, salary, hireDate</p>
     * 
     * @return byte array containing the CSV data
     * @throws RuntimeException if an error occurs during export
     */
    @Transactional(readOnly = true)
    public byte[] exportEmployeesToCSV() {
        return exportEmployeesToCSV(emp.findAll());
    }

    /**
     * Exports specified employees to CSV format.
     * 
     * <p>Generates a CSV file with the following columns:
     * firstName, lastName, email, department, jobTitle, phoneNumber, salary, hireDate</p>
     * 
     * @param employees list of employees to export
     * @return byte array containing the CSV data
     * @throws RuntimeException if an error occurs during export
     */
    @Transactional(readOnly = true)
    public byte[] exportEmployeesToCSV(List<Employees> employees) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(outputStream)) {
            pw.println("firstName,lastName,email,department,jobTitle,phoneNumber,salary,hireDate");
            for (Employees employee : employees) {
                pw.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getEmail(),
                    employee.getDepartment(),
                    employee.getJobTitle(),
                    employee.getPhoneNumber(),
                    employee.getSalary(),
                    employee.getHireDate() != null ? employee.getHireDate() : ""
                ));
            }
        }
        return outputStream.toByteArray();
    }
}
