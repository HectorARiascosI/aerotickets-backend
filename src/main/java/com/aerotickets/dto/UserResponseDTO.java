package com.aerotickets.dto;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private boolean enabled;
    private Instant createdAt;
}