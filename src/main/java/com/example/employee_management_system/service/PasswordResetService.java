package com.example.employee_management_system.service;

import com.example.employee_management_system.exception.AuthenticationException;
import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.exception.UserAlreadyExistsException;
import com.example.employee_management_system.model.PasswordResetToken;
import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.repository.PasswordResetTokenRepository;
import com.example.employee_management_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.password.reset.token.expiry.minutes:5}")
    private int tokenExpiryMinutes;

    @Autowired
    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createAndSendPasswordResetToken(String email) {
        // Find user by email through employee association using efficient query
        Users user = userRepository.findByEmployeeEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + email));

        // Check if user already has a valid reset token
        if (tokenRepository.existsByUserAndUsedFalse(user)) {
            throw new UserAlreadyExistsException("Password reset email already sent. Please check your email.");
        }

        // Create reset code
        String code = generateRandomCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(tokenExpiryMinutes);

        PasswordResetToken resetToken = new PasswordResetToken(code, user, expiryDate);
        tokenRepository.save(resetToken);

        // Send password reset email with code
        emailService.sendPasswordResetEmail(email, code, user.getUserName());
    }

    @Transactional
    public void resetPassword(String code, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByCode(code)
                .orElseThrow(() -> new AuthenticationException("Invalid reset code"));

        if (!resetToken.isValid()) {
            if (resetToken.isExpired()) {
                throw new AuthenticationException("Reset code has expired");
            } else {
                throw new AuthenticationException("Reset code has already been used");
            }
        }

        // Mark token as used
        tokenRepository.markTokenAsUsed(code);

        // Update user password
        Users user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Send password change notification
        emailService.sendPasswordChangedNotification(user.getEmployees().getEmail(), user.getUserName());
    }

    @Transactional
    public void validateResetToken(String code) {
        PasswordResetToken resetToken = tokenRepository.findByCode(code)
                .orElseThrow(() -> new AuthenticationException("Invalid reset code"));

        if (!resetToken.isValid()) {
            if (resetToken.isExpired()) {
                throw new AuthenticationException("Reset code has expired");
            } else {
                throw new AuthenticationException("Reset code has already been used");
            }
        }
    }

    private String generateRandomCode() {
        // Generate a random 6-digit code
        return String.format("%06d", (int)(Math.random() * 1000000));
    }

    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteExpiredTokens(now);
    }
}
