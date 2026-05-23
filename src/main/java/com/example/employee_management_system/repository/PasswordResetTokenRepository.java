package com.example.employee_management_system.repository;

import com.example.employee_management_system.model.PasswordResetToken;
import com.example.employee_management_system.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByCode(String code);

    Optional<PasswordResetToken> findByUser(Users user);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < :expiryDate")
    /// by me
    /// @Param is used for parameter binding
    void deleteExpiredTokens(@Param("expiryDate") LocalDateTime expiryDate);

    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.code = :code")
    void markTokenAsUsed(@Param("code") String code);

    boolean existsByUserAndUsedFalse(Users user);
}
