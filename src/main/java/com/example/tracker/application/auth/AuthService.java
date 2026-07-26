package com.example.tracker.application.auth;

import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<AuthResult> loginWithProvider(String providerKey, String rawProviderToken);

    Mono<AuthResult> refresh(String refreshToken);

    Mono<Void> logout(String refreshToken);

    record AuthResult(String accessToken, String refreshToken, long expiresInSeconds) {
    }
}
