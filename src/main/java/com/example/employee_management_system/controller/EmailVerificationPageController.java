package com.example.employee_management_system.controller;

import com.example.employee_management_system.annotation.RateLimited;
import com.example.employee_management_system.dto.ChangePasswordRequest;
import com.example.employee_management_system.dto.EmailVerificationCodeRequest;
import com.example.employee_management_system.dto.VerifyEmailStepRequest;
import com.example.employee_management_system.service.EmailVerificationService;
import com.example.employee_management_system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmailVerificationPageController {

    private final EmailVerificationService emailVerificationService;
    private final UserService userService;

    @Autowired
    public EmailVerificationPageController(EmailVerificationService emailVerificationService, UserService userService) {
        this.emailVerificationService = emailVerificationService;
        this.userService = userService;
    }

    @GetMapping("/verify-email-step")
    public String verifyEmailStepPage(Model model) {
        model.addAttribute("verifyEmailStepRequest", new VerifyEmailStepRequest());
        return "verify-email-step";
    }

    @PostMapping("/verify-email-step")
    public String handleVerifyEmailStep(@Valid @ModelAttribute("verifyEmailStepRequest") VerifyEmailStepRequest request,
                                         BindingResult bindingResult,
                                         Model model,
                                         Authentication authentication,
                                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "verify-email-step";
        }

        try {
            String username = authentication.getName();
            // Update user's email and send verification code
            userService.updateUserEmail(username, request.getEmail());
            emailVerificationService.resendVerificationEmail(username);
            redirectAttributes.addFlashAttribute("message", "Verification code sent to your email. Please check your inbox.");
            return "redirect:/verify-email";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to send verification email: " + e.getMessage());
            return "verify-email-step";
        }
    }

    @GetMapping("/verify-email")
    public String verifyEmailPage(Model model) {
        model.addAttribute("verificationRequest", new EmailVerificationCodeRequest());
        return "verify-email";
    }

    @GetMapping("/verify-success")
    public String verifySuccessPage(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Email verified successfully! Please change your password.");
        return "redirect:/change-password";
    }

    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String handleChangePassword(@Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequest request,
                                       BindingResult bindingResult,
                                       Model model,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes,
                                       HttpServletRequest httpRequest) {
        if (bindingResult.hasErrors()) {
            return "change-password";
        }

        if (!request.getNewPassword().equals(request.getPasswordConfirm())) {
            model.addAttribute("error", "Passwords do not match");
            return "change-password";
        }

        try {
            String username = authentication.getName();
            userService.changePassword(username, request.getNewPassword());
            // Invalidate the session to force re-login
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            redirectAttributes.addFlashAttribute("message", "Password changed successfully! Please login with your new password.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to change password. Please try again.");
            return "change-password";
        }
    }

    @PostMapping("/verify-email")
    public String handleVerification(@Valid @ModelAttribute("verificationRequest") EmailVerificationCodeRequest request,
                                      BindingResult bindingResult,
                                      Model model,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "verify-email";
        }

        try {
            emailVerificationService.verifyEmail(request.getCode());
            redirectAttributes.addFlashAttribute("message", "Email verified successfully! Please change your password.");
            return "redirect:/change-password";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid verification code. Please try again.");
            return "verify-email";
        }
    }

    @RateLimited(limit = 3, period = 3600) // 3 requests per hour
    @PostMapping("/resend-verification")
    public String resendVerification(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            emailVerificationService.resendVerificationEmail(username);
            redirectAttributes.addFlashAttribute("message", "Verification code sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to resend verification code: " + e.getMessage());
        }
        return "redirect:/verify-email";
    }
}
