package com.example.tracker.security;

import java.util.UUID;

import com.example.tracker.domain.user.Role;

/**
 * UserPrinciple
 */
public record UserPrincipal(UUID userId, String email, Role role) {
}
