package com.aerotickets.controller;

import com.aerotickets.constants.AuthConstants;
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

@RestController
@RequestMapping(AuthConstants.BASE_PATH)
@CrossOrigin(origins = AuthConstants.CORS_ORIGIN_LOCAL, allowCredentials = "true")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping(AuthConstants.REGISTER_PATH)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(java.util.Map.of("message", AuthConstants.MSG_EMAIL_ALREADY_REGISTERED));
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(java.util.Map.of("message", AuthConstants.MSG_USER_REGISTERED_SUCCESS));
    }

    @PostMapping(AuthConstants.LOGIN_PATH)
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException(AuthConstants.MSG_USER_NOT_FOUND));

        String token = jwtService.generateToken(user);

        AuthResponse response = new AuthResponse(
                token,
                user.getFullName(),
                user.getEmail()
        );

        return ResponseEntity.ok(response);
    }
}