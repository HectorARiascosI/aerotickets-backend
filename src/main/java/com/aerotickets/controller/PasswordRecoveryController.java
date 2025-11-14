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

@RestController
@RequestMapping("/auth")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "https://aerotickets-frontend.vercel.app",
                "https://aerotickets-frontend-iaqxjc453-hector-riascos-projects.vercel.app"
        },
        allowCredentials = "true"
)
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

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }

        userRepository.findByEmail(email).ifPresent(user -> {
            String token = jwtUtil.generateTemporaryToken(user.getEmail(), 10);
            String resetUrl = frontendBaseUrl + "/reset-password/" + token;

            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);

            log.info("Solicitud de recuperación procesada para usuario registrado.");
        });

        return ResponseEntity.ok(
                Map.of("message", "Si el correo está registrado, te hemos enviado un enlace para restablecer tu contraseña.")
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("password");
        if (newPassword == null) {
            newPassword = body.get("newPassword");
        }

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token de recuperación es obligatorio");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña es obligatoria");
        }

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