package com.example.employee_management_system.controller;

import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.securityConfig.CustomUserDetailsService;
import com.example.employee_management_system.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final AuthenticationService authenticationService;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public LoginController(AuthenticationService authenticationService, CustomUserDetailsService userDetailsService) {
        this.authenticationService = authenticationService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/login")
    public String loginPage(Model model, Authentication authentication) {
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                Users user = userDetailsService.getUserByUsername(authentication.getName());
                if (user.isTemporaryPassword() && !user.isEmailVerified()) {
                    return "redirect:/verify-email-step";
                } else if (user.isTemporaryPassword()) {
                    return "redirect:/change-password";
                } else if (!user.isEmailVerified()) {
                    return "redirect:/verify-email-step";
                }
                return "redirect:/dashboard";
            }
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/login-success")
    public String loginSuccess(Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            Users user = userDetailsService.getUserByUsername(authentication.getName());
            if (user.isTemporaryPassword() && !user.isEmailVerified()) {
                redirectAttributes.addFlashAttribute("message", "Please change your temporary password and verify your email.");
                return "redirect:/verify-email-step";
            } else if (user.isTemporaryPassword()) {
                redirectAttributes.addFlashAttribute("message", "Please change your temporary password.");
                return "redirect:/change-password";
            } else if (!user.isEmailVerified()) {
                redirectAttributes.addFlashAttribute("message", "Please verify your email address.");
                return "redirect:/verify-email-step";
            }
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/signup")
    public String signupPage(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Registration is now handled by administrators. Please contact your administrator to create your account.");
        return "redirect:/login";
    }

    @PostMapping("/signup")
    public String handleSignup(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Registration is now handled by administrators. Please contact your administrator to create your account.");
        return "redirect:/login";
    }

}
