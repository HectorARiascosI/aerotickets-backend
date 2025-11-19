package com.aerotickets.controller;

import com.aerotickets.constants.UserConstants;
import com.aerotickets.dto.UserRegistrationDTO;
import com.aerotickets.dto.UserResponseDTO;
import com.aerotickets.entity.User;
import com.aerotickets.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing user registration.
 * Returns a UserResponseDTO with the created user's id (no password).
 */
@RestController
@RequestMapping(UserConstants.BASE_PATH)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(UserConstants.REGISTER_PATH)
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