package com.aerotickets.controller;

import com.aerotickets.dto.UserRegistrationDTO;
import com.aerotickets.dto.UserResponseDTO;
import com.aerotickets.entity.User;
import com.aerotickets.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing user registration.
 * Now returns a UserResponseDTO with the created user's id (no password).
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor-based dependency injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user and returns a safe DTO with user id and basic info.
     * @param dto User registration data
     * @return ResponseEntity<UserResponseDTO>
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegistrationDTO dto) {
        User u = userService.register(dto.getFullName(), dto.getEmail(), dto.getPassword());

        UserResponseDTO resp = UserResponseDTO.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .createdAt(u.getCreatedAt())
                .build();

        return ResponseEntity.ok(resp);
    }
}