package com.example.employee_management_system.repository;

import com.example.employee_management_system.model.EmailVerificationToken;
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
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByCode(String code);

    Optional<EmailVerificationToken> findByUser(Users user);

    /// by me
    /// @Modifying is used for modifying the database
    /// Without @Modifying, Spring assumes the query is a read-only SELECT query.
    /// Using UPDATE or DELETE queries without @Modifying usually causes runtime exceptions.
    /// @Transactional is used for transaction management
    /// @Query is used for custom query
    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiryDate < :expiryDate")
    void deleteExpiredTokens(@Param("expiryDate") LocalDateTime expiryDate);
}
