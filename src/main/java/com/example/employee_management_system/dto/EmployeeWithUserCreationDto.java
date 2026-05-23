package com.example.employee_management_system.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

@JsonPropertyOrder({"id", "firstName", "lastName", "email", "phoneNumber", "hireDate", "jobTitle", "salary", "department", "username", "temporaryPassword", "roles", "authorities"})
public class EmployeeWithUserCreationDto {

    private Integer id;

    @NotNull(message = "First name is required")
    @Size(max = 100, message = "First name cannot be longer than 100 characters")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot be longer than 100 characters")
    private String lastName;

    @Size(max = 150, message = "Email cannot be longer than 150 characters")
    @Email(message = "Please enter a valid email address")
    private String email;

    @Size(max = 20, message = "Phone number cannot be longer than 20 characters")
    private String phoneNumber;

    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;

    @Size(max = 100, message = "Job title cannot be longer than 100 characters")
    private String jobTitle;

    private Float salary;

    @Size(max = 100, message = "Department cannot be longer than 100 characters")
    private String department;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Temporary password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String temporaryPassword;

    private Set<String> roles;

    private Set<String> authorities;

    // Default constructor
    public EmployeeWithUserCreationDto() {}

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public Float getSalary() { return salary; }
    public void setSalary(Float salary) { this.salary = salary; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTemporaryPassword() { return temporaryPassword; }
    public void setTemporaryPassword(String temporaryPassword) { this.temporaryPassword = temporaryPassword; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public Set<String> getAuthorities() { return authorities; }
    public void setAuthorities(Set<String> authorities) { this.authorities = authorities; }
}
