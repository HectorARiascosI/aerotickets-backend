package com.aerotickets.controller;

import com.aerotickets.entity.User;
import com.aerotickets.repository.UserRepository;
import com.aerotickets.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador para recuperación de contraseña:
 *  - POST /auth/forgot-password  -> genera token temporal y "envía" enlace
 *  - POST /auth/reset-password   -> valida token y actualiza la contraseña
 *
 * Las respuestas usan JSON simple: { "message": "..." } para que el frontend
 * pueda mostrar mensajes amigables.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class PasswordRecoveryController {

    private static final Logger log = LoggerFactory.getLogger(PasswordRecoveryController.class);

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

    /**
     * Paso 1: el usuario envía su correo para solicitar recuperación.
     * Body esperado: { "email": "usuario@correo.com" }
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con ese correo"));

        // Token válido por 10 minutos
        String token = jwtUtil.generateTemporaryToken(user.getEmail(), 10);

        // En un entorno real, aquí enviarías un correo.
        // Por ahora dejamos la URL en logs/console para desarrollo:
        String pathUrl = "http://localhost:5173/reset-password/" + token;
        String queryUrl = "http://localhost:5173/reset-password?token=" + token;

        log.info("🔐 Enlace de recuperación (path):  {}", pathUrl);
        log.info("🔐 Enlace de recuperación (query): {}", queryUrl);

        System.out.println("Password reset (path): " + pathUrl);
        System.out.println("Password reset (query): " + queryUrl);

        // Devolvemos un mensaje genérico para el usuario
        return ResponseEntity.ok(
                Map.of("message", "Si el correo está registrado, te hemos enviado un enlace para restablecer tu contraseña.")
        );
    }

    /**
     * Paso 2: el usuario envía el token y la nueva contraseña.
     * Body esperado (compatibles):
     *  - { "token": "...", "password": "nuevaClave" }
     *  - { "token": "...", "newPassword": "nuevaClave" }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("password"); // compat con el front actual

        if (newPassword == null) {
            newPassword = body.get("newPassword"); // compat si el front envía 'newPassword'
        }

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token de recuperación es obligatorio");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña es obligatoria");
        }

        // Valida token (firma y expiración); lanza IllegalArgumentException si no sirve
        String email = jwtUtil.validateTemporaryToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o usuario no encontrado"));

        user.setPasswordHash(encoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of("message", "Tu contraseña ha sido actualizada correctamente. Ya puedes iniciar sesión.")
        );
    }
}