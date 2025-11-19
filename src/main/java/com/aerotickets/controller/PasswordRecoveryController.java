package com.aerotickets.controller;

import com.aerotickets.constants.AuthConstants;
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
@RequestMapping(AuthConstants.BASE_PATH)
@CrossOrigin(
        origins = {
                AuthConstants.CORS_ORIGIN_LOCAL,
                AuthConstants.CORS_ORIGIN_VERCEL_MAIN,
                AuthConstants.CORS_ORIGIN_VERCEL_ENV
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

    @PostMapping(AuthConstants.FORGOT_PASSWORD_PATH)
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get(AuthConstants.FIELD_EMAIL);

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(AuthConstants.MSG_EMAIL_REQUIRED);
        }

        userRepository.findByEmail(email).ifPresent(user -> {
            String token = jwtUtil.generateTemporaryToken(user.getEmail(), AuthConstants.TEMP_TOKEN_MINUTES);
            String resetUrl = frontendBaseUrl + AuthConstants.RESET_PASSWORD_PATH + "/" + token;

            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);

            log.info("Solicitud de recuperación procesada para usuario registrado.");
        });

        return ResponseEntity.ok(
                Map.of(AuthConstants.FIELD_MESSAGE, AuthConstants.MSG_PASSWORD_RESET_SENT)
        );
    }

    @PostMapping(AuthConstants.RESET_PASSWORD_PATH)
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get(AuthConstants.FIELD_TOKEN);
        String newPassword = body.get(AuthConstants.FIELD_PASSWORD);
        if (newPassword == null) {
            newPassword = body.get(AuthConstants.FIELD_NEW_PASSWORD);
        }

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(AuthConstants.MSG_TOKEN_REQUIRED);
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException(AuthConstants.MSG_NEW_PASSWORD_REQUIRED);
        }

        String email = jwtUtil.validateTemporaryToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(AuthConstants.MSG_INVALID_TOKEN_OR_USER));

        user.setPasswordHash(encoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of(AuthConstants.FIELD_MESSAGE, AuthConstants.MSG_PASSWORD_UPDATED)
        );
    }
}