package com.aerotickets.security;

import com.aerotickets.constants.JwtConstants;
import com.aerotickets.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretValue;

    @Value("${jwt.expiration-minutes:120}")
    private long expirationMinutes;

    private Key getSigningKey() {
        byte[] keyBytes = secretValue.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    JwtConstants.SECRET_TOO_SHORT_PREFIX + keyBytes.length
            );
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        long expirationMs = expirationMinutes * 60 * 1000;
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim(JwtConstants.CLAIM_NAME, user.getFullName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractUsername(String token) {
        return extractEmail(token);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}