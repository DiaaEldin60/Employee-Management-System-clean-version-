package com.example.employee_management_system.controller;

import com.example.employee_management_system.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetPageController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetPageController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email,
                                       RedirectAttributes redirectAttributes) {
        try {
            passwordResetService.createAndSendPasswordResetToken(email);
            redirectAttributes.addFlashAttribute("message", "Password reset email sent! Please check your inbox.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send reset email. Please try again.");
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String token, Model model) {
        if (token == null || token.isEmpty()) {
            return "redirect:/forgot-password";
        }
        
        try {
            // Validate token
            passwordResetService.validateResetToken(token);
            model.addAttribute("token", token);
            return "reset-password";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid or expired token");
            return "reset-password";
        }
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String token,
                                       @RequestParam String password,
                                       @RequestParam String confirmPassword,
                                       HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/reset-password?token=" + token + "&error=match";
        }

        try {
            passwordResetService.resetPassword(token, password);
            redirectAttributes.addFlashAttribute("message", "Password reset successfully! Please login with your new password.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reset password. The link may have expired.");
            return "redirect:/reset-password?token=" + token + "&error=expired";
        }
    }
}
