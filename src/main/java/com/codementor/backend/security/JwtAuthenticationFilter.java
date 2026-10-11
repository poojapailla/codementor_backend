package com.codementor.backend.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Reads the {@code Authorization: Bearer <token>} header, validates the JWT,
 * and populates the security context with the resolved user. Invalid or
 * expired tokens simply leave the context empty so protected endpoints
 * respond with HTTP 401.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            try {
                JWTClaimsSet claims = jwtService.validate(
                        header.substring(BEARER_PREFIX.length()).trim()
                );

                CurrentUser currentUser = new CurrentUser(
                        Long.valueOf(claims.getSubject()),
                        String.valueOf(claims.getClaim("email")),
                        String.valueOf(claims.getClaim("role"))
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                currentUser,
                                null,
                                List.of(new SimpleGrantedAuthority(
                                        "ROLE_" + currentUser.role()
                                ))
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            } catch (RuntimeException invalid) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}