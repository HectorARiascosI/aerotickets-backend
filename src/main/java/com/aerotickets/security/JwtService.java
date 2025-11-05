package com.aerotickets.security;

import com.aerotickets.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${vueler.jwt.secret}")
    private String secretValue;

    @Value("${vueler.jwt.expiration-minutes:120}")
    private long expirationMinutes;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretValue.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        long expirationMs = expirationMinutes * 60 * 1000;
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("name", user.getFullName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // Alias para compatibilidad con filtros que llamaban extractUsername
    public String extractUsername(String token) {
        return extractEmail(token);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
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