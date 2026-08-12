package com.example.tracker.security.oauth;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.tracker.exceptions.UnsupportedAuthProviderException;

@Component
public class OAuthProviderFactory {
    private final Map<String, OAuthProvider> providerByKey;

    public OAuthProviderFactory(List<OAuthProvider> providers) {
        this.providerByKey = providers.stream()
                // function.identity means use 'verifyAndFetchUserInfo' as value
                .collect(Collectors.toMap(p -> p.providerKey().toUpperCase(), Function.identity()));
    }

    public OAuthProvider resolve(String providerKey) {
        OAuthProvider provider = providerByKey.get(providerKey.toUpperCase());

        if (provider == null) {
            throw new UnsupportedAuthProviderException("Provider not found " + providerKey);
        }

        return provider;
    }
}
