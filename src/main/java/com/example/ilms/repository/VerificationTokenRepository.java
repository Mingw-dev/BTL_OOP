package com.example.ilms.repository;

import com.example.ilms.entity.VerificationToken;
import com.example.ilms.type.TokenStatus;
import com.example.ilms.type.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    @Modifying
    @Query("UPDATE VerificationToken vt SET vt.status = com.example.ilms.type.TokenStatus.REVOKED " +
            "WHERE vt.email = :email AND vt.type = :type AND vt.status = com.example.ilms.type.TokenStatus.PENDING"
    )
    void revokeAllPendingTokens(@Param("email") String email, @Param("type") TokenType type);
    
    Optional<VerificationToken> findByTokenAndTypeAndStatus(String token, TokenType type, TokenStatus status);
}
