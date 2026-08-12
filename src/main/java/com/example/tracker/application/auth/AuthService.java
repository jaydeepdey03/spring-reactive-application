package com.example.tracker.application.auth;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<AuthResult> loginWithProvider(String providerKey, String rawProviderToken);

    Mono<AuthResult> refresh(String refreshToken);

    Mono<Void> logout(String refreshToken);

    Mono<Void> logoutAllForUser(UUID userId);

    record AuthResult(String accessToken, String refreshToken, long expiresInSeconds,
            com.example.tracker.domain.user.User user) {
    }
}
