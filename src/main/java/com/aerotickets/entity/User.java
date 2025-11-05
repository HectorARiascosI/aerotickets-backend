package com.aerotickets.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @Column(nullable = false, length = 120)
    private String passwordHash;
    
    @Builder.Default
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // ✅ Campos necesarios para autenticación
    @Builder.Default
    @Column(nullable = false)
    private String role = "USER"; // puedes cambiar por "ADMIN" o lo que necesites
    
    
    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true; // indica si la cuenta está activa

    // ✅ Getters y Setters (redundantes pero seguros para Eclipse)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}