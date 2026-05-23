package com.example.employee_management_system.service;

import com.example.employee_management_system.exception.AuthenticationException;
import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.exception.UserAlreadyExistsException;
import com.example.employee_management_system.model.EmailVerificationToken;
import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.repository.EmailVerificationTokenRepository;
import com.example.employee_management_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationService {


    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final CacheManager cacheManager;

    @Value("${app.email.verification.token.expiry.minutes:5}")
    private int tokenExpiryMinutes;

    @Autowired
    public EmailVerificationService(EmailVerificationTokenRepository tokenRepository, UserRepository userRepository, EmailService emailService, CacheManager cacheManager) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.cacheManager = cacheManager;
    }

    @Transactional
    public void createAndSendVerificationToken(String username) {
        Users user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        // Check if user already has a valid verification token
        Optional<EmailVerificationToken> existingToken = tokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            throw new UserAlreadyExistsException("Verification email already sent. Please check your email.");
        }

        // Create verification code
        String code = generateRandomCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(tokenExpiryMinutes);

        EmailVerificationToken verificationToken = new EmailVerificationToken(code, user, expiryDate);
        tokenRepository.save(verificationToken);

        // Send verification email with code
        emailService.sendEmailVerification(user.getEmployees().getEmail(), code, user.getUserName());
    }

    @Transactional
    public void verifyEmail(String code) {
        EmailVerificationToken verificationToken = tokenRepository.findByCode(code)
                .orElseThrow(() -> new AuthenticationException("Invalid verification code"));

        if (verificationToken.isExpired()) {
            throw new AuthenticationException("Verification code has expired");
        }

        // Enable user account and mark email as verified
        Users user = verificationToken.getUser();
        String username = user.getUserName();
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setTemporaryPassword(false);
        userRepository.save(user);

        // Clear all user-related caches to ensure fresh data on next login
        if (cacheManager != null) {
            clearUserCaches(username);
        }

        // Delete the token after successful verification
        tokenRepository.delete(verificationToken);
    }

    private String generateRandomCode() {
        // Generate a random 6-digit code
        return String.format("%06d", (int)(Math.random() * 1000000));
    }

    private void clearUserCaches(String username) {
        String[] cacheNames = {"user-entities-by-username", "users", "users-by-username", "user-entities-by-email"};
        for (String cacheName : cacheNames) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(username);
            }
        }
    }

    @Transactional
    public void resendVerificationEmail(String username) {
        Users user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        // Delete existing tokens
        Optional<EmailVerificationToken> existingToken = tokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            tokenRepository.delete(existingToken.get());
        }

        // Create new verification code
        String code = generateRandomCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(tokenExpiryMinutes);

        EmailVerificationToken verificationToken = new EmailVerificationToken(code, user, expiryDate);
        tokenRepository.save(verificationToken);

        // Send verification email with code
        emailService.sendEmailVerification(user.getEmployees().getEmail(), code, user.getUserName());
    }

    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteExpiredTokens(now);
    }

    public boolean isEmailVerified(String username) {
        Users user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        // Check if user's email is verified
        return user.isEmailVerified();
    }
}
