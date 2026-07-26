package com.example.tracker.exceptions;

public class OAuthVerificationException extends RuntimeException {
    public OAuthVerificationException(String message) {
        super(message);
    }
}
