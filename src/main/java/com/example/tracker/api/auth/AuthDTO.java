package com.example.tracker.api.auth;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public class AuthDTO {
    public record LoginRequest(
            @NotBlank String provider, // "GOOGLE" for now, extensible
            @NotBlank String token // provider ID token
    ) {
    }

    public record RefreshRequest(@NotBlank String refreshToken) {
    }

    public record UserDto(UUID id, String email, String displayName, String role) {
    }

    public record TokenResponse(String accessToken, String refreshToken, long expiresIn, String tokenType,
            UserDto user) {
    }
}
