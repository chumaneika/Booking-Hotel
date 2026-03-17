package com.booking_hotel.auth_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours default
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private Long refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Email from token (subject or explicit "email" claim). */
    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        String email = claims.get("email", String.class);
        return email != null ? email : claims.getSubject();
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object value = claims.get("userId");
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails, Long userId) {
        return generateAccessToken(userDetails, userId);
    }

    public String generateAccessToken(UserDetails userDetails, Long userId) {
        String email = userDetails.getUsername();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("tokenType", "ACCESS");
        claims.put("roles", extractRoles(userDetails));
        return createToken(claims, email, expiration);
    }

    public String generateRefreshToken(UserDetails userDetails, Long userId) {
        String email = userDetails.getUsername();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("tokenType", "REFRESH");
        claims.put("jti", UUID.randomUUID().toString());
        return createToken(claims, email, refreshExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, Long ttlMs) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ttlMs))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean validateRefreshToken(String token, UserDetails userDetails) {
        return validateToken(token, userDetails) && "REFRESH".equals(extractTokenType(token));
    }

    public String extractTokenType(String token) {
        return extractAllClaims(token).get("tokenType", String.class);
    }

    public Instant extractExpirationInstant(String token) {
        return extractExpiration(token).toInstant();
    }

    private List<String> extractRoles(UserDetails userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 bytes) for HS256");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
