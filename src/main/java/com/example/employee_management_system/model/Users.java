package com.example.employee_management_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


/**
 * Entity class representing a user account in the system.
 * 
 * <p>This entity stores user authentication information, account status, and
 * role assignments. Users are linked to employees through a one-to-one relationship,
 * enabling employees to access the system with specific permissions.</p>
 * 
 * <p>Security features include:</p>
 * <ul>
 *   <li>Password hashing (handled by UserService)</li>
 *   <li>Temporary password flag for onboarding</li>
 *   <li>Email verification status tracking</li>
 *   <li>Account enabled/disabled status</li>
 * </ul>
 * 
 * <p>The entity implements role-based access control through many-to-many
 * relationships with the Roles entity.</p>
 * 
 * @author Diaa Eldin
 * @version 1.0.0
 * @since 2025-12-02
 * @see com.example.employee_management_system.model.Employees
 * @see com.example.employee_management_system.model.Roles
 * @see jakarta.persistence.Entity
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username", unique = true),
    @Index(name = "idx_user_enabled", columnList = "enabled")
})
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private int id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name="username",nullable = false, unique = true, length = 50)
    private String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Column(name = "password",nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean isTemporaryPassword;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Employees employees;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<Roles> roles=new HashSet<>();

    public Users() {

    }

    public Users(String userName, String password, boolean enabled, Employees employees, Set<Roles> roles) {
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
        this.employees = employees;
        this.roles = roles;
    }
    public Users(String userName, String password, boolean enabled, Employees employees) {
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
        this.employees = employees;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isTemporaryPassword() {
        return isTemporaryPassword;
    }

    public void setTemporaryPassword(boolean temporaryPassword) {
        isTemporaryPassword = temporaryPassword;
    }


    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Employees getEmployees() {
        return employees;
    }

    public void setEmployees(Employees employees) {
        this.employees = employees;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
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
        return "Users{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
