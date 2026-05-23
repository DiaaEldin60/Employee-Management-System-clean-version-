package com.example.employee_management_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "authorities", indexes = {
    @Index(name = "idx_authority", columnList = "authority", unique = true)
})
public class Authorities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Authority name is required")
    @Size(max = 50, message = "Authority name cannot be longer than 50 characters")
    @Column(name = "authority", nullable = false, unique = true, length = 50)
    private String name;

    // Default constructor
    public Authorities() {}

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
