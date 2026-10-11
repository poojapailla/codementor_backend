package com.codementor.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Returns the authenticated user resolved from the validated JWT.
     * Protected endpoints are only reached with a valid token, so this
     * never needs a client-supplied user ID.
     */
    public static CurrentUser currentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.getPrincipal() instanceof CurrentUser currentUser) {
            return currentUser;
        }

        throw new IllegalStateException("No authenticated user in the security context.");
    }

    public static Long currentUserId() {
        return currentUser().id();
    }
}