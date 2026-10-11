package com.codementor.backend.security;

/**
 * Thrown when a JWT cannot be trusted (malformed, tampered, or expired).
 * Reaching a protected endpoint with a rejected token yields HTTP 401.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}