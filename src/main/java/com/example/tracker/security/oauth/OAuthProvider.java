package com.example.tracker.security.oauth;

import reactor.core.publisher.Mono;

public interface OAuthProvider {
    String providerKey();

    Mono<OAuthUserInfo> verifyAndFetchUserInfo(String token);
}
