package com.example.tracker.domain.user;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {

    Mono<User> findByAuthProviderAndProviderUserId(String authProvider, String providerUserId);

    Mono<Boolean> existsByEmail(String email);
}