package com.example.employee_management_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_role", columnList = "role", unique = true)
})
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name cannot be longer than 50 characters")
    @Column(name = "role", nullable = false, unique = true, length = 50)
    private String role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_authorities",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private Set<Authorities> authorities = new HashSet<>();

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Users> users = new HashSet<>();

    // Default constructor
    public Roles() {}

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Set<Authorities> getAuthorities() { return authorities; }
    public void setAuthorities(Set<Authorities> authorities) { this.authorities = authorities; }

    public Set<Users> getUsers() { return users; }
    public void setUsers(Set<Users> users) { this.users = users; }
}
