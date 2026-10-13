package com.codementor.backend.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class JwtService {

    private static final JWSAlgorithm ALGORITHM = JWSAlgorithm.HS256;

    private final javax.crypto.SecretKey secretKey;

    private final long expirationMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "app.jwt.secret must be at least 32 characters long."
            );
        }

        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                ALGORITHM.getName()
        );
        this.expirationMinutes = expirationMinutes;
    }

    public String issueToken(Long userId, String email, String role) {
        Instant now = Instant.now();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .build();

        try {
            SignedJWT jwt = new SignedJWT(
                    new JWSHeader(ALGORITHM),
                    claims
            );
            jwt.sign(new MACSigner(secretKey));
            return jwt.serialize();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to sign authentication token.", exception);
        }
    }

    /**
     * Validates the signature and expiration, then returns the claims.
     * Throws {@link InvalidTokenException} for malformed, tampered, or
     * expired tokens so callers can treat them as unauthenticated.
     */
    public JWTClaimsSet validate(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            if (!jwt.verify(new MACVerifier(secretKey))) {
                throw new InvalidTokenException("Token signature is invalid.");
            }

            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            Date expiration = claims.getExpirationTime();

            if (expiration == null || expiration.before(new Date())) {
                throw new InvalidTokenException("Token has expired.");
            }

            if (claims.getSubject() == null) {
                throw new InvalidTokenException("Token does not carry a user identity.");
            }

            return claims;
        } catch (java.text.ParseException exception) {
            throw new InvalidTokenException("Token is malformed.");
        } catch (InvalidTokenException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new InvalidTokenException("Token could not be validated.");
        }
    }
}