package com.aerotickets.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // Permitir configurar orígenes por ENV en Render:
        // CORS_ALLOWED_ORIGINS = http://localhost:5173,https://aerotickets-frontend.vercel.app,https://*.vercel.app
        String defaults =
                "http://localhost:5173," +
                "http://127.0.0.1:5173," +
                "https://aerotickets-frontend.vercel.app," +
                "https://aerotickets-frontend-git-main-hector-riascos-projects.vercel.app," +
                "https://*.vercel.app";

        String env = System.getenv().getOrDefault("CORS_ALLOWED_ORIGINS", defaults);

        String[] patterns = Arrays.stream(env.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        registry.addMapping("/**")
                // 👇 Usa patrones para soportar wildcard (*.vercel.app)
                .allowedOriginPatterns(patterns)
                // Métodos permitidos (incluye OPTIONS y PATCH)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // Cabeceras típicas
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type")
                // Si manejas cookies o Authorization en fetch, debe ser true
                .allowCredentials(true)
                // Cachea el preflight por 1 hora
                .maxAge(3600);
    }
}