package com.codementor.backend.security;

import org.junit.jupiter.api.Test;

import com.nimbusds.jwt.JWTClaimsSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET =
            "unit-test-secret-that-is-long-enough-for-hs256-1234567890";

    private JwtService service(long expirationMinutes) {
        return new JwtService(SECRET, expirationMinutes);
    }

    @Test
    void issuesACompactSignedToken() {
        String token = service(60).issueToken(42L, "a@b.c", "USER");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);
        assertFalse(token.contains(SECRET));
    }

    @Test
    void validateReturnsTheIssuedClaims() {
        JwtService jwtService = service(60);
        String token = jwtService.issueToken(123L, "test@example.com", "ADMIN");

        JWTClaimsSet claims = jwtService.validate(token);

        assertEquals("123", claims.getSubject());
        assertEquals(
                "test@example.com",
                String.valueOf(claims.getClaim("email"))
        );
        assertEquals("ADMIN", String.valueOf(claims.getClaim("role")));
    }

    @Test
    void validateRejectsTamperedToken() {
        JwtService jwtService = service(60);
        String token = jwtService.issueToken(7L, "a@b.c", "USER");

        String tampered = token.substring(0, token.length() - 2)
                + (token.endsWith("aa") ? "bb" : "aa");

        assertThrows(
                InvalidTokenException.class,
                () -> jwtService.validate(tampered)
        );
    }

    @Test
    void validateRejectsMalformedToken() {
        JwtService jwtService = service(60);

        assertThrows(
                InvalidTokenException.class,
                () -> jwtService.validate("not-a-jwt")
        );
        assertThrows(
                InvalidTokenException.class,
                () -> jwtService.validate("")
        );
        assertThrows(
                InvalidTokenException.class,
                () -> jwtService.validate(null)
        );
    }

    @Test
    void validateRejectsExpiredToken() {
        JwtService jwtService = service(0);
        String token = jwtService.issueToken(1L, "a@b.c", "USER");

        assertThrows(
                InvalidTokenException.class,
                () -> jwtService.validate(token)
        );
    }

    @Test
    void constructorRejectsShortSecret() {
        assertThrows(
                IllegalStateException.class,
                () -> new JwtService("too-short", 60)
        );
    }
}