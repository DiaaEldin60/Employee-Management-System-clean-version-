package com.example.employee_management_system.controller;

import com.example.employee_management_system.dto.CreateUserRequest;
import com.example.employee_management_system.dto.RoleUpdateRequest;
import com.example.employee_management_system.dto.UserDto;
import com.example.employee_management_system.mapper.UserMapper;
import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "User management APIs")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create user with temporary password for employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-with-temp-password")
    public ResponseEntity<UserDto> createUserWithTemporaryPassword(@Valid @RequestBody CreateUserRequest request) {
        Users user = userService.createUserWithTemporaryPassword(
            request.getEmployeeId(),
            request.getUsername(),
            request.getTemporaryPassword(),
            request.getRoles(),
            request.getAuthorities()
        );
        UserDto userDto = UserMapper.INSTANCE.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable int id) {
        Users user = userService.getUserById(id);
        UserDto userDto = UserMapper.INSTANCE.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Get all users with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy)
    {
        Pageable pageable = PageRequest.of(page, Math.min(size, 20), Sort.by(sortBy));
        Page<Users> usersPage = userService.getAllUsers(pageable);
        Page<UserDto> userDtos = usersPage.map(UserMapper.INSTANCE::toDto);

        return ResponseEntity.ok(userDtos);
    }

    @Operation(summary = "Update user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable int id,
                                           @Valid @RequestBody UserDto updatedUserDto) {
        Users user = UserMapper.INSTANCE.toEntity(updatedUserDto);
        Users updated = userService.updateUser(id, user);
        UserDto updatedDto = UserMapper.INSTANCE.toDto(updated);
        return ResponseEntity.ok(updatedDto);
    }
    @Operation(summary = "Partially update user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> patchUpdateUser(
            @PathVariable int id, @RequestBody Map<String, Object> updates)
    {
        Users updated = userService.patchUser(id, updates);
        UserDto updatedDto = UserMapper.INSTANCE.toDto(updated);
        return ResponseEntity.ok(updatedDto);
    }
    @Operation(summary = "Delete user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user roles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User roles updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "User or role not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles")
    public ResponseEntity<UserDto> updateUserRoles(@PathVariable int id,
                                                   @Valid @RequestBody RoleUpdateRequest roleUpdateRequest) {
        roleUpdateRequest.setUserId(id);
        Users updated = userService.updateUserRoles(roleUpdateRequest.getUserId(), roleUpdateRequest.getRoleNames());
        UserDto updatedDto = UserMapper.INSTANCE.toDto(updated);
        return ResponseEntity.ok(updatedDto);
    }

    @Operation(summary = "Upload users from CSV file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid CSV format"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadUsers(@RequestParam("file") MultipartFile file)
    {
        List<Users> uploaded = userService.uploadUsersFromCSV(file);
        return ResponseEntity.ok(Map.of(
            "message", "Users uploaded successfully",
            "count", uploaded.size(),
            "users", uploaded
        ));
    }

    @Operation(summary = "Export users to CSV")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CSV file generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportUsers()
    {
        byte[] csvData = userService.exportUsersToCSV();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=users.csv")
            .header("Content-Type", "text/csv")
            .body(csvData);
    }

}
