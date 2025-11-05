package com.aerotickets.controller;

import com.aerotickets.entity.User;
import com.aerotickets.model.AuthResponse;
import com.aerotickets.model.LoginRequest;
import com.aerotickets.model.RegisterRequest;
import com.aerotickets.repository.UserRepository;
import com.aerotickets.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para registro y autenticación de usuarios.
 * Proporciona endpoints públicos:
 *  - POST /api/auth/register
 *  - POST /api/auth/login
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * Registro de un nuevo usuario.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya está registrado.");
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    /**
     * Inicio de sesión con generación de JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        // Autenticar credenciales del usuario
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, user));
    }
}