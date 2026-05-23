package com.example.employee_management_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * Entity class representing an employee in the system.
 * 
 * <p>This entity stores all employee-related information including personal details,
 * contact information, job information, and compensation. Each employee is associated
 * with exactly one user account through a one-to-one relationship.</p>
 * 
 * <p>The entity uses JPA auditing to automatically track creation and modification timestamps.</p>
 * 
 * <p>Database constraints:</p>
 * <ul>
 *   <li>Email must be unique across all employees</li>
 *   <li>First name and last name are required</li>
 *   <li>Hire date is required</li>
 * </ul>
 * 
 * @author Diaa Eldin
 * @version 1.0.0
 * @since 2025-12-02
 * @see com.example.employee_management_system.model.Users
 * @see jakarta.persistence.Entity
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonPropertyOrder({"id", "firstName", "lastName", "email", "phoneNumber", "hireDate", "jobTitle", "salary", "department"})
@Table(name = "employees", indexes = {
    @Index(name = "idx_employee_email", columnList = "email"),
    @Index(name = "idx_employee_department", columnList = "department"),
    @Index(name = "idx_employee_hire_date", columnList = "hire_date")
})
public class Employees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "First name is required")
    @Size(max = 100, message = "First name cannot be longer than 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot be longer than 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull(message = "Email name is required")
    @Size(max = 150, message = "Email cannot be longer than 150 characters")
    @Email(message = "Please enter a valid email address")
    @Column(name = "email", nullable = false,unique = true, length = 150)
    private String email;

    @Size(max = 20, message = "phone number cannot be longer than 20 characters")
    @Column(name = "phone_number",  length = 20)
    private String phoneNumber;

    @NotNull(message = "Hire Date is required")
    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Size(max = 100, message = "job title cannot be longer than 100 characters")
    @Column(name = "job_title",  length = 100)
    private String jobTitle;

    @Column(name = "salary")
    private float salary;

    @Size(max = 100, message = "Department cannot be longer than 100 characters")
    @Column(name = "department", length = 100)
    private String department;





    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    @JoinColumn(name = "user_id", unique = true, nullable = true)
    @JsonIgnore
    private Users user;





    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    public Employees() {

    }

    public Employees(Employees other, int id) {
        this.id=id;
        this.department=other.getDepartment();
        this.email = other.getEmail();
        this.salary = other.getSalary();
        this.firstName=other.getFirstName();
        this.lastName= other.getLastName();
        this.hireDate = other.getHireDate();
        this.jobTitle= other.getJobTitle();
        this.phoneNumber = other.getPhoneNumber();
        this.user=other.user;
    }
    public Employees(String department, Float salary, String jobTitle, LocalDate hireDate, String phoneNumber, String email, String lastName, String firstName, Users user) {
        this.department = department;
        this.salary = salary;
        this.jobTitle = jobTitle;
        this.hireDate = hireDate;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.user = user;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Employees{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", hireDate=" + hireDate +
                ", jobTitle='" + jobTitle + '\'' +
                ", salary=" + salary +
                ", department='" + department + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
