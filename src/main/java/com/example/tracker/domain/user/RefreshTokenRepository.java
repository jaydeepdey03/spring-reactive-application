package com.example.tracker.domain.user;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, UUID> {
    Mono<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

    Flux<RefreshToken> findAllByUserIdAndRevokedFalse(UUID userId);
}
