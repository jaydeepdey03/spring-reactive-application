package com.example.tracker.api.advice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import com.example.tracker.exceptions.ForbiddenException;
import com.example.tracker.exceptions.InvalidRefreshTokenException;
import com.example.tracker.exceptions.JwtValidationException;
import com.example.tracker.exceptions.OAuthVerificationException;
import com.example.tracker.exceptions.ResourceNotFoundException;
import com.example.tracker.exceptions.UnsupportedAuthProviderException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleNotFound(ResourceNotFoundException ex,
            ServerWebExchange exchange) {
        logException(exchange, ex, HttpStatus.NOT_FOUND);
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleForbidden(ForbiddenException ex,
            ServerWebExchange exchange) {
        logException(exchange, ex, HttpStatus.FORBIDDEN);
        return body(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({ JwtValidationException.class, InvalidRefreshTokenException.class })
    public Mono<ResponseEntity<Map<String, Object>>> handleAuthFailure(RuntimeException ex,
            ServerWebExchange exchange) {
        logException(exchange, ex, HttpStatus.UNAUTHORIZED);
        return body(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(OAuthVerificationException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleOAuthFailure(OAuthVerificationException ex,
            ServerWebExchange exchange) {
        logException(exchange, ex, HttpStatus.UNAUTHORIZED);
        return body(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UnsupportedAuthProviderException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleUnsupportedProvider(UnsupportedAuthProviderException ex,
            ServerWebExchange exchange) {
        logException(exchange, ex, HttpStatus.BAD_REQUEST);
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleOptimisticLock(OptimisticLockingFailureException ex,
            ServerWebExchange exchange) {
        logException(exchange, ex, HttpStatus.CONFLICT);
        return body(HttpStatus.CONFLICT,
                "This record was modified concurrently by another request. Please retry.");
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidation(WebExchangeBindException ex,
            ServerWebExchange exchange) {
        String details = ex.getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        logException(exchange, ex, HttpStatus.BAD_REQUEST);
        return body(HttpStatus.BAD_REQUEST, details);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGeneric(Exception ex,
            ServerWebExchange exchange) {
        logException(exchange, ex, HttpStatus.INTERNAL_SERVER_ERROR);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    private Mono<ResponseEntity<Map<String, Object>>> body(HttpStatus status, String message) {
        return Mono.just(ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "message", message)));
    }

    private void logException(ServerWebExchange exchange, Exception ex, HttpStatus status) {
        log.error("Request failed: {} {} -> {}", exchange.getRequest().getMethod(),
                exchange.getRequest().getPath(), status.value(), ex);
    }
}
