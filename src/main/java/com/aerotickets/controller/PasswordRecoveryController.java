package com.aerotickets.controller;

import com.aerotickets.entity.User;
import com.aerotickets.repository.UserRepository;
import com.aerotickets.security.JwtUtil;
import com.aerotickets.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador para recuperación de contraseña:
 *  - POST /auth/forgot-password  -> genera token temporal y envía enlace por email
 *  - POST /auth/reset-password   -> valida token y actualiza la contraseña
 *
 * Respuestas JSON: { "message": "..." }
 */
@RestController
@RequestMapping("/auth") // 👈 IMPORTANTE: se mantiene así para que funcione con tu context-path /api
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class PasswordRecoveryController {

    private static final Logger log = LoggerFactory.getLogger(PasswordRecoveryController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    public PasswordRecoveryController(
            UserRepository userRepository,
            PasswordEncoder encoder,
            JwtUtil jwtUtil,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    /**
     * Paso 1: el usuario envía su correo para solicitar recuperación.
     * Body esperado: { "email": "usuario@correo.com" }
     *
     * PRIVACIDAD:
     * - Siempre responde 200 con mensaje genérico, exista o no el usuario.
     * - No revela si el email está registrado (buena práctica de seguridad / habeas data).
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }

        userRepository.findByEmail(email).ifPresent(user -> {
            // Solo si el usuario existe generamos token y enviamos correo
            String token = jwtUtil.generateTemporaryToken(user.getEmail(), 10);
            String resetUrl = frontendBaseUrl + "/reset-password/" + token;

            // Envío real de correo
            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);

            // Log interno (sin token ni email completo)
            log.info("🔐 Solicitud de recuperación procesada para usuario registrado.");
        });

        // Mensaje siempre genérico
        return ResponseEntity.ok(
                Map.of("message", "Si el correo está registrado, te hemos enviado un enlace para restablecer tu contraseña.")
        );
    }

    /**
     * Paso 2: el usuario envía el token y la nueva contraseña.
     * Body esperado:
     *  - { "token": "...", "password": "nuevaClave" }
     *  o { "token": "...", "newPassword": "nuevaClave" }
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

        // Valida token (firma y expiración). Si es inválido/expirado, JwtUtil lanza excepción.
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