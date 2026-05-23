package com.example.employee_management_system.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class RoleUpdateRequest {
    
    @NotNull(message = "User ID is required")
    private Integer userId;
    
    @NotEmpty(message = "At least one role is required")
    private Set<String> roleNames;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Set<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(Set<String> roleNames) {
        this.roleNames = roleNames;
    }
}
