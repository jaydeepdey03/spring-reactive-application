package com.example.tracker.api.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracker.application.auth.AuthService;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public Mono<AuthDTO.TokenResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        return authService.loginWithProvider(request.provider(), request.token())
                .map(result -> new AuthDTO.TokenResponse(
                        result.accessToken(), result.refreshToken(), result.expiresInSeconds()));
    }

    @PostMapping("/auth/refresh")
    public Mono<AuthDTO.TokenResponse> refresh(@Valid @RequestBody AuthDTO.RefreshRequest request) {
        return authService.refresh(request.refreshToken())
                .map(result -> new AuthDTO.TokenResponse(
                        result.accessToken(), result.refreshToken(), result.expiresInSeconds()));
    }

    @PostMapping("/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> logout(@Valid @RequestBody AuthDTO.RefreshRequest request) {
        return authService.logout(request.refreshToken());
    }
}
