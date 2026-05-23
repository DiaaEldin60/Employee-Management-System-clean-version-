package com.example.employee_management_system.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@JsonPropertyOrder({"firstName", "lastName", "email", "phoneNumber", "hireDate", "jobTitle", "salary", "department"})
public class EmployeePatchDto {

    @Size(max = 100, message = "First name cannot be longer than 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name cannot be longer than 100 characters")
    private String lastName;

    @Size(max = 150, message = "Email cannot be longer than 150 characters")
    @Email(message = "Please enter a valid email address")
    private String email;

    @Size(max = 20, message = "Phone number cannot be longer than 20 characters")
    private String phoneNumber;
    
    private LocalDate hireDate;
    
    @Size(max = 100, message = "Job title cannot be longer than 100 characters")
    private String jobTitle;
    
    private Float salary;
    
    @Size(max = 100, message = "Department cannot be longer than 100 characters")
    private String department;

    // Default constructor
    public EmployeePatchDto() {}

    // Getters and setters
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
