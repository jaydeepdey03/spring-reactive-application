package com.example.tracker.security.jwt;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.example.tracker.security.UserPrincipal;

import reactor.core.publisher.Mono;

public class JWTAuthenticationFilter implements WebFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    public final JWTService jwtService;

    public JWTAuthenticationFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange);
        }

        String token = header.substring(BEARER_PREFIX.length());

        return Mono.fromCallable(() -> jwtService.parseAndValidateAccessToken(token)).flatMap(claims -> {
            UserPrincipal userPrincipal = new UserPrincipal(claims.userId(), claims.email(), claims.role());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userPrincipal, null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + claims.role().toString())));
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        }).onErrorResume(ex -> chain.filter(exchange));
    }
}
