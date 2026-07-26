package com.example.tracker.security.oauth;

public record OAuthUserInfo(String providerUserId, String email, String displayName) {
}