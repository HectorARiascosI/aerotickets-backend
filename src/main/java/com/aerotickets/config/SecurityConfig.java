package com.aerotickets.config;

import com.aerotickets.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Lista de orígenes permitidos. Puedes sobreescribirla en Render con la
     * variable de entorno CORS_ALLOWED_ORIGINS (coma-separados).
     *
     * Ejemplos:
     *   http://localhost:5173,
     *   https://aerotickets-frontend.vercel.app,
     *   https://*.vercel.app
     */
    @Value("${cors.allowed-origins:http://localhost:5173,https://aerotickets-frontend.vercel.app,https://aerotickets-frontend-git-main-hector-riascos-projects.vercel.app,https://*.vercel.app}")
    private String allowedOriginsProp;

    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        return http
                // ✅ Habilita CORS y usa nuestra configuración
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ✅ MUY IMPORTANTE: permitir preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/auth/**", "/live/**", "/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/flights/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // Normaliza la lista (coma-separada) desde propiedades/ENV
        List<String> raw = Arrays.stream(allowedOriginsProp.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toList());

        // Separa los que tienen wildcard (*) para setAllowedOriginPatterns
        List<String> patterns = raw.stream().filter(s -> s.contains("*")).toList();
        List<String> exact = raw.stream().filter(s -> !s.contains("*")).toList();

        if (!exact.isEmpty()) {
            // Orígenes exactos
            cfg.setAllowedOrigins(exact);
        }
        if (!patterns.isEmpty()) {
            // Patrones con wildcard (e.g. https://*.vercel.app)
            cfg.setAllowedOriginPatterns(patterns);
        }

        // Métodos y cabeceras típicos
        cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        cfg.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        cfg.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}