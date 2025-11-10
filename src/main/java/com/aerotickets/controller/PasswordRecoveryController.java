package com.aerotickets.controller;

import com.aerotickets.entity.User;
import com.aerotickets.repository.UserRepository;
import com.aerotickets.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class PasswordRecoveryController {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public PasswordRecoveryController(
            UserRepository userRepository,
            PasswordEncoder encoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email requerido");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para ese email"));

        String token = jwtUtil.generateTemporaryToken(user.getEmail(), 10);

        // Imprime ambas variantes de URL para comodidad (tu front usa path param)
        System.out.println("Password reset (path): http://localhost:5173/reset-password/" + token);
        System.out.println("Password reset (query): http://localhost:5173/reset-password?token=" + token);

        return ResponseEntity.ok("Hemos enviado un enlace de recuperación a tu correo.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("password"); // compat con front actual
        if (newPassword == null) {
            newPassword = body.get("newPassword"); // compat si el cliente manda 'newPassword'
        }

        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Token y nueva contraseña son requeridos");
        }

        String email = jwtUtil.validateTemporaryToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));

        user.setPasswordHash(encoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña actualizada correctamente.");
    }
}