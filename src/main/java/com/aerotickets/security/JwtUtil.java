package com.aerotickets.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Utility class for generating and validating temporary JWTs
 * (for example password recovery or email confirmation links).
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes:120}") long expirationMinutes
    ) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret must be at least 32 characters (256 bits). Current length: " + bytes.length
            );
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expirationMs = expirationMinutes * 60_000;
    }

    /** Generate a general-purpose token for a given subject. */
    public String generate(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Generate a short-lived temporary token (e.g. password reset). */
    public String generateTemporaryToken(String subject, int minutes) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(minutes * 60L)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Validate a temporary token and return the subject if valid. */
    public String validateTemporaryToken(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
    }

    /** Extract the subject from any valid token. */
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    /** Check if a token is valid and correctly signed. */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            System.out.println("⚠️ Invalid token: " + e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}