package com.example.tracker.api.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracker.application.auth.AuthService;
import com.example.tracker.security.UserPrincipal;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Mono<AuthDTO.TokenResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        return authService.loginWithProvider(request.provider(), request.token())
                .map(result -> new AuthDTO.TokenResponse(
                        result.accessToken(),
                        result.refreshToken(),
                        result.expiresInSeconds(),
                        "Bearer",
                        result.user() == null ? null
                                : new AuthDTO.UserDto(
                                        result.user().getId(),
                                        result.user().getEmail(),
                                        result.user().getDisplayName(),
                                        result.user().getRole() == null ? null : result.user().getRole().name())));
    }

    @PostMapping("/refresh")
    public Mono<AuthDTO.TokenResponse> refresh(@Valid @RequestBody AuthDTO.RefreshRequest request) {
        return authService.refresh(request.refreshToken())
                .map(result -> new AuthDTO.TokenResponse(
                        result.accessToken(),
                        result.refreshToken(),
                        result.expiresInSeconds(),
                        "Bearer",
                        result.user() == null ? null
                                : new AuthDTO.UserDto(
                                        result.user().getId(),
                                        result.user().getEmail(),
                                        result.user().getDisplayName(),
                                        result.user().getRole() == null ? null : result.user().getRole().name())));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> logout(@RequestBody(required = false) AuthDTO.RefreshRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (request != null && request.refreshToken() != null && !request.refreshToken().isBlank()) {
            return authService.logout(request.refreshToken());
        }
        if (principal != null) {
            return authService.logoutAllForUser(principal.userId());
        }
        return Mono.empty();
    }
}
