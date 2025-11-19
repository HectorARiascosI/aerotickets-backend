package com.aerotickets.security;

import com.aerotickets.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Slf4j
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
                    JwtConstants.SECRET_TOO_SHORT_PREFIX + bytes.length
            );
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expirationMs = expirationMinutes * 60_000;
    }

    public String generate(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTemporaryToken(String subject, int minutes) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(minutes * 60L)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateTemporaryToken(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (JwtException e) {
            throw new IllegalArgumentException(JwtConstants.TEMP_TOKEN_INVALID_OR_EXPIRED);
        }
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
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