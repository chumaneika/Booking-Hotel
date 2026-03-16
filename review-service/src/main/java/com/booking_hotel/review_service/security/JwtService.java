package com.booking_hotel.review_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Object rolesClaim = extractAllClaims(token).get("roles");

        if (rolesClaim == null) {
            return Collections.emptyList();
        }

        if (rolesClaim instanceof Collection<?> roleCollection) {
            return roleCollection.stream()
                    .map(Object::toString)
                    .map(this::normalizeRole)
                    .toList();
        }

        if (rolesClaim instanceof String rolesString) {
            return List.of(rolesString.split(",")).stream()
                    .map(String::trim)
                    .filter(role -> !role.isEmpty())
                    .map(this::normalizeRole)
                    .toList();
        }

        return Collections.emptyList();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration() == null || claims.getExpiration().getTime() > System.currentTimeMillis();
        } catch (Exception ignored) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 bytes) for HS256");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String normalizeRole(String role) {
        String trimmed = role == null ? "" : role.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }

        String upper = trimmed.toUpperCase(Locale.ROOT);
        return upper.startsWith("ROLE_") ? upper : "ROLE_" + upper;
    }

}
