package com.example.tracker.security.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.tracker.exceptions.OAuthVerificationException;

import reactor.core.publisher.Mono;

@Component
public class GoogleOAuthProvider implements OAuthProvider {

    private final WebClient webClient;
    private final String expectedClientId;
    private final String tokenInfoUri;

    public GoogleOAuthProvider(WebClient.Builder webClientBuilder,
            @Value("${app.oauth.google.client-id}") String clientId,
            @Value("${app.oauth.google.tokeninfo-uri}") String tokenUri) {
        this.webClient = webClientBuilder.build();
        this.expectedClientId = clientId;
        this.tokenInfoUri = tokenUri;
    }

    @Override
    public String providerKey() {
        return "Google";
    };

    @Override
    public Mono<OAuthUserInfo> verifyAndFetchUserInfo(String idToken) { // idtoken we get from client (client gets from
                                                                        // google servers)
        return webClient.get()
                .uri(tokenInfoUri + "?id_token={token}", idToken)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        resp -> Mono.error(new OAuthVerificationException("Invalid Google ID")))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(body -> (Map<String, Object>) body)
                .flatMap(this::validateAudienceAndMap);
    };

    public Mono<OAuthUserInfo> validateAudienceAndMap(Map<String, Object> claims) {
        Object aud = claims.get("aud");

        if (aud == null || !expectedClientId.equals(aud.toString())) {
            return Mono.error(new OAuthVerificationException("Token Mismatch"));
        }

        Object sub = claims.get("sub");
        Object email = claims.get("email");
        Object name = claims.get("name");

        if (sub == null || email == null) {
            return Mono.error(new OAuthVerificationException("Google token missing claims"));
        }

        return Mono.just(new OAuthUserInfo(sub.toString(), email.toString(), name != null ? name.toString() : null));
    }

}
