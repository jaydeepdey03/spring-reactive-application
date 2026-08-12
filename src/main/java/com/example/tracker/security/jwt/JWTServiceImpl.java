package com.example.tracker.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.tracker.domain.user.Role;
import com.example.tracker.exceptions.JwtValidationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTServiceImpl implements JWTService {

    private final SecretKey signingKey;
    private final String issuer;
    private final Duration accessTokenTtl;
    private final SecureRandom secureRandom = new SecureRandom();

    private JWTServiceImpl(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.access-token-ttl-minutes}") long accessTokenTtl) {

        this.signingKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));

        this.issuer = issuer;
        this.accessTokenTtl = Duration.ofMinutes(accessTokenTtl);
    }

    @Override
    public String generateAccessToken(UUID userId, String email, Role role) {

        Instant now = Instant.now();

        return Jwts.builder()
                .issuer(issuer)
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenTtl)))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String generateRefreshToken() {

        byte[] bytes = new byte[64];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    @Override
    public TokenClaims parseAndValidateAccessToken(String token) {

        try {

            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID userId = UUID.fromString(claims.getSubject());

            String email = claims.get("email", String.class);

            String roleStr = claims.get("role", String.class);

            Role role = roleStr == null
                    ? Role.USER
                    : Role.valueOf(roleStr);

            return new TokenClaims(
                    userId,
                    email,
                    role);

        } catch (ExpiredJwtException e) {

            throw new JwtValidationException(
                    "Access token expired");

        } catch (JwtException | IllegalArgumentException e) {

            throw new JwtValidationException(
                    "Invalid access token");
        }
    }
}