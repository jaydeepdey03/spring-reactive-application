package com.example.tracker.security.jwt;

import java.util.UUID;

import com.example.tracker.domain.user.Role;

public interface JWTService {
    String generateAccessToken(UUID userId, String email, Role role);

    String generateRefreshToken();

    TokenClaims parseAndValidateAccessToken(String token); // validate token

    record TokenClaims(UUID userId, String email, Role role) {
    }
}
