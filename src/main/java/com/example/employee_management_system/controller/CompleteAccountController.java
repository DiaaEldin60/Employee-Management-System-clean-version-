package com.example.employee_management_system.controller;

import com.example.employee_management_system.dto.CompleteAccountRequest;
import com.example.employee_management_system.exception.UserAlreadyExistsException;
import com.example.employee_management_system.service.EmailVerificationService;
import com.example.employee_management_system.service.UserService;
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
public class CompleteAccountController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @Autowired
    public CompleteAccountController(UserService userService, EmailVerificationService emailVerificationService) {
        this.userService = userService;
        this.emailVerificationService = emailVerificationService;
    }

    @GetMapping("/complete-account")
    public String completeAccountPage(Model model) {
        model.addAttribute("completeAccountRequest", new CompleteAccountRequest());
        return "complete-account";
    }

    @PostMapping("/complete-account")
    public String handleCompleteAccount(@Valid @ModelAttribute("completeAccountRequest") CompleteAccountRequest request,
                                       BindingResult bindingResult,
                                       Model model,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "complete-account";
        }

        if (!request.getNewPassword().equals(request.getPasswordConfirm())) {
            model.addAttribute("error", "Passwords do not match");
            return "complete-account";
        }

        try {
            String username = authentication.getName();
            userService.completeAccount(username, request.getEmail(), request.getNewPassword());
            
            // Send email verification
            emailVerificationService.createAndSendVerificationToken(username);
            
            redirectAttributes.addFlashAttribute("message", "Account completed successfully. Please verify your email.");
            return "redirect:/verify-email";
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("error", "Email already in use. Please use a different email.");
            return "complete-account";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "complete-account";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to complete account. Please try again.");
            return "complete-account";
        }
    }
}
