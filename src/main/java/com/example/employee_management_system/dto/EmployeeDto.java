package com.example.employee_management_system.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@JsonPropertyOrder({"id", "firstName", "lastName", "email", "phoneNumber", "hireDate", "jobTitle", "salary", "department"})
public class EmployeeDto {

    private Integer id;

    @NotNull(message = "First name is required")
    @Size(max = 100, message = "First name cannot be longer than 100 characters")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot be longer than 100 characters")
    private String lastName;

    @NotNull(message = "Email is required")
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

    // Default constructor
    public EmployeeDto() {}

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
}
