package com.example.employee_management_system.service;

import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.model.Employees;
import com.example.employee_management_system.model.Roles;
import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.repository.AuthorityRepository;
import com.example.employee_management_system.repository.EmployeeRepository;
import com.example.employee_management_system.repository.RoleRepository;
import com.example.employee_management_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1);
        testUser.setUserName("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEnabled(true);
    }

    @Test
    void testCreateUserWithTemporaryPassword() {
        Employees employee = new Employees();
        employee.setId(1);
        Roles role = new Roles();
        role.setRole("EMPLOYEE");

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(roleRepository.findByRole("EMPLOYEE")).thenReturn(Optional.of(role));
        when(userRepository.save(any(Users.class))).thenReturn(testUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        Users result = userService.createUserWithTemporaryPassword(1, "testuser", "password", java.util.Set.of("EMPLOYEE"), java.util.Set.of());

        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        verify(userRepository, times(1)).save(any(Users.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void testGetAllUsers() {
        List<Users> users = Arrays.asList(testUser);
        Page<Users> page = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<Users> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        Users result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999));
        verify(userRepository, times(1)).findById(999);
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(1);

        userService.deleteUser(1);

        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(999));
        verify(userRepository, never()).deleteById(anyInt());
    }

    @Test
    void testUpdateUser_Success() {
        Users updatedUser = new Users();
        updatedUser.setUserName("updateduser");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        Users result = userService.updateUser(1, updatedUser);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(Users.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        Users updatedUser = new Users();
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(999, updatedUser));
        verify(userRepository, never()).save(any(Users.class));
    }
}
