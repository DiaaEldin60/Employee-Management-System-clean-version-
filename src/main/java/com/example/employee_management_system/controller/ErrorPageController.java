package com.example.employee_management_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/error")
public class ErrorPageController {

    @GetMapping("/{errorCode}")
    public String handleError(@PathVariable String errorCode, HttpServletRequest request, Model model) {
        model.addAttribute("status", errorCode);
        model.addAttribute("message", getErrorMessage(errorCode));
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "error/" + errorCode;
    }

    private String getErrorMessage(String errorCode) {
        switch (errorCode) {
            case "403":
                return "You don't have permission to access this resource";
            case "404":
                return "The page you are looking for doesn't exist or has been moved";
            case "500":
                return "Something went wrong on our end. Please try again later";
            default:
                return "An error occurred";
        }
    }
}
