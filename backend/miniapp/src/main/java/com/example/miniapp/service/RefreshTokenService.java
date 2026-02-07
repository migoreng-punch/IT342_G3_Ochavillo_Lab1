package com.example.miniapp.service;

import com.example.miniapp.entity.RefreshToken;
import com.example.miniapp.entity.User;
import com.example.miniapp.repository.RefreshTokenRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    public String create(User user) {
        String rawToken = generateToken();
        String hash = hash(rawToken);

        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(hash);
        rt.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));

        repository.save(rt);
        return rawToken;
    }

    public User validate(String rawToken) {
        String hash = hash(rawToken);

        RefreshToken rt = repository.findByTokenHashAndRevokedFalse(hash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (rt.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return rt.getUser();
    }

    private String generateToken() {
        return UUID.randomUUID() + "." + UUID.randomUUID();
    }

    private String hash(String token) {
        return DigestUtils.sha256Hex(token);
    }

    public void revoke(String rawToken) {
        String hash = hash(rawToken);

        repository.findByTokenHashAndRevokedFalse(hash)
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    repository.save(rt);
                });
    }

}
