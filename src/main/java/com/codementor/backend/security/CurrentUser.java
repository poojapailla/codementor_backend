package com.codementor.backend.security;

/**
 * Resolved from the validated JWT. This is the only trustworthy identity
 * inside the application; client-supplied IDs are never used for ownership.
 */
public record CurrentUser(Long id, String email, String role) {
}