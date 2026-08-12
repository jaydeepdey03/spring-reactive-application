package com.example.tracker.application.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.tracker.domain.user.RefreshToken;
import com.example.tracker.domain.user.RefreshTokenRepository;
import com.example.tracker.domain.user.Role;
import com.example.tracker.domain.user.User;
import com.example.tracker.domain.user.UserRepository;
import com.example.tracker.exceptions.InvalidRefreshTokenException;
import com.example.tracker.security.jwt.JWTService;
import com.example.tracker.security.oauth.OAuthProviderFactory;
import com.example.tracker.security.oauth.OAuthUserInfo;

import reactor.core.publisher.Mono;

@Service
public class AuthServiceImpl implements AuthService {

    private final OAuthProviderFactory providerFactory;
    private final JWTService jwtService;
    private final long accessTokenTtlSeconds;
    private final Duration refreshTokenTtl;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public AuthServiceImpl(OAuthProviderFactory providerFactory, JWTService jwtService, UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            @Value("${app.jwt.access-token-ttl-minutes}") long accessTokenTtlMin,
            @Value("${app.jwt.refresh-token-ttl-days}") long refreshTokenTtlDays) {
        this.accessTokenTtlSeconds = accessTokenTtlMin * 60;
        this.providerFactory = providerFactory;
        this.refreshTokenTtl = Duration.ofDays(refreshTokenTtlDays);
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;

    }

    // get provider from providerkey, then create user in the db, issue tokens
    @Override
    public Mono<AuthResult> loginWithProvider(String providerKey, String rawProviderToken) {
        return providerFactory.resolve(providerKey).verifyAndFetchUserInfo(rawProviderToken)
                .flatMap(user -> findOrCreateUser(providerKey.toUpperCase(), user)).flatMap(this::issueTokens);
    };

    @Override
    public Mono<AuthResult> refresh(String refreshToken) {
        String hash = sha256(refreshToken);
        return refreshTokenRepository
                .findByTokenHashAndRevokedFalse(hash)
                .switchIfEmpty(Mono.error(new InvalidRefreshTokenException("Refresh token not found or revoked")))
                .filter(tk -> tk.getExpiresAt().isAfter(Instant.now()))
                .switchIfEmpty(Mono.error(new InvalidRefreshTokenException("Refresh token expired")))
                .flatMap(tk -> userRepository.findById(tk.getUserId())
                        // revoke existing refresh token, issue new one
                        .flatMap(user -> revoke(tk).then(issueTokens(user))));
    };

    @Override
    public Mono<Void> logout(String refreshToken) {
        String hash = sha256(refreshToken);
        return refreshTokenRepository.findByTokenHashAndRevokedFalse(hash).flatMap(this::revoke).then();
    };

    @Override
    public Mono<Void> logoutAllForUser(UUID userId) {
        return refreshTokenRepository.findAllByUserIdAndRevokedFalse(userId)
                .flatMap(this::revoke)
                .then();
    }

    private Mono<User> findOrCreateUser(String providerKey, OAuthUserInfo user) {
        return userRepository.findByAuthProviderAndProviderUserId(providerKey, user.providerUserId())
                .switchIfEmpty(Mono.defer(() -> {
                    Instant instant = Instant.now();
                    User newuser = User.builder().email(user.email()).displayName(user.displayName()).role(Role.USER)
                            .providerUserId(user.providerUserId()).createdAt(instant).updatedAt(instant)
                            .authProvider(providerKey).build();

                    return userRepository.save(newuser);
                }));
    }

    private Mono<RefreshToken> revoke(RefreshToken token) {
        token.setRevoked(true);
        return refreshTokenRepository.save(token);
    }

    private Mono<AuthResult> issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(),
                user.getRole() != null ? user.getRole() : Role.USER);
        String generatedRefreshToken = jwtService.generateRefreshToken();
        Instant now = Instant.now();
        Instant expiry = Instant.now().plus(refreshTokenTtl);
        RefreshToken refreshToken = RefreshToken.builder().tokenHash(sha256(generatedRefreshToken)).userId(user.getId())
                .createdAt(now).expiresAt(expiry).revoked(false).build();
        return refreshTokenRepository.save(refreshToken)
                .thenReturn(new AuthResult(accessToken, generatedRefreshToken, accessTokenTtlSeconds, user));

    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
