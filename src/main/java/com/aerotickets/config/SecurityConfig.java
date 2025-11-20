package com.aerotickets.config;

import com.aerotickets.constants.SecurityConstants;
import com.aerotickets.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final Environment environment;

    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, SecurityConstants.ANT_PATTERN_ALL).permitAll()
                        .requestMatchers(SecurityConstants.PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(SecurityConstants.AUTH_ENDPOINTS).permitAll()
                        .requestMatchers(SecurityConstants.LIVE_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, SecurityConstants.CATALOG_GET_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, SecurityConstants.FLIGHTS_GET_ENDPOINTS).permitAll()
                        .requestMatchers("/payments/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        String raw = environment.getProperty(
                SecurityConstants.CORS_ALLOWED_ORIGINS_PROPERTY,
                SecurityConstants.DEFAULT_ALLOWED_ORIGINS
        );

        List<String> allowed = Arrays.stream(raw.split(SecurityConstants.CORS_ORIGINS_SEPARATOR))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        List<String> patterns = allowed.stream()
                .filter(s -> s.contains("*"))
                .collect(Collectors.toList());

        List<String> exact = allowed.stream()
                .filter(s -> !s.contains("*"))
                .collect(Collectors.toList());

        CorsConfiguration cfg = new CorsConfiguration();
        if (!exact.isEmpty()) {
            cfg.setAllowedOrigins(exact);
        }
        if (!patterns.isEmpty()) {
            cfg.setAllowedOriginPatterns(patterns);
        }
        cfg.setAllowedMethods(Arrays.asList(SecurityConstants.CORS_ALLOWED_METHODS));
        cfg.setAllowedHeaders(Arrays.asList(SecurityConstants.CORS_ALLOWED_HEADERS));
        cfg.setExposedHeaders(Arrays.asList(SecurityConstants.CORS_EXPOSED_HEADERS));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(SecurityConstants.CORS_MAX_AGE_SECONDS);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(SecurityConstants.ANT_PATTERN_ALL, cfg);
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