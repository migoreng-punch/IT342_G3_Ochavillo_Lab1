package com.example.miniapp.repository;

import com.example.miniapp.entity.User;
import com.example.miniapp.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByExpiryDateBefore(Instant expiryDate);

    Optional<VerificationToken>
    findByUserAndUsedFalse(User user);
}
