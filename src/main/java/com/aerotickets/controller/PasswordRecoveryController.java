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

    public PasswordRecoveryController(UserRepository userRepository, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    // 📧 Enviar enlace temporal de recuperación
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con ese correo"));

        // Generar token de recuperación válido por 10 minutos
        String token = jwtUtil.generateTemporaryToken(user.getEmail(), 10);

        // En un sistema real aquí se enviaría un correo, por ahora imprimimos el enlace
        System.out.println("🔗 Enlace de recuperación: http://localhost:5173/reset-password?token=" + token);

        return ResponseEntity.ok("Se ha enviado un enlace de recuperación a tu correo electrónico.");
    }

    // 🔒 Restablecer contraseña usando token temporal
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        String email = jwtUtil.validateTemporaryToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));

        user.setPasswordHash(encoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña actualizada exitosamente.");
    }
}