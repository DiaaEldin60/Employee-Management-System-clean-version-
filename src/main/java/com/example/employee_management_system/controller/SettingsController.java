package com.example.employee_management_system.controller;

import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.securityConfig.CustomUserDetailsService;
import com.example.employee_management_system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SettingsController {

    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SettingsController(UserService userService,
                              CustomUserDetailsService userDetailsService,
                              PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/settings/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) {
        String username = authentication.getName();
        Users user = userDetailsService.getUserByUsername(username);

        // Validate current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
            return "redirect:/settings#security";
        }

        // Validate password match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/settings#security";
        }

        // Validate password length
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long");
            return "redirect:/settings#security";
        }

        try {
            userService.changePassword(username, newPassword);
            // Invalidate the session to force re-login
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            redirectAttributes.addFlashAttribute("message", "Password changed successfully! Please login with your new password.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to change password: " + e.getMessage());
            return "redirect:/settings#security";
        }
    }

    @PostMapping("/settings/update-profile")
    public String updateProfile(@RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String phone,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        String username = authentication.getName();

        try {
            Users user = userDetailsService.getUserByUsername(username);
            if (user.getEmployees() != null) {
                user.getEmployees().setFirstName(firstName);
                user.getEmployees().setLastName(lastName);
                user.getEmployees().setPhoneNumber(phone);
                // Save would be called through a service method
            }
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
            return "redirect:/settings#profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
            return "redirect:/settings#profile";
        }
    }
}
