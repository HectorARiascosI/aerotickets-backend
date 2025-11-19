package com.aerotickets.config;

import com.aerotickets.constants.CorsConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        String env = System.getenv().getOrDefault(
                CorsConstants.CORS_ALLOWED_ORIGINS_ENV,
                CorsConstants.CORS_DEFAULT_ALLOWED_ORIGINS
        );

        String[] patterns = Arrays.stream(env.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        registry.addMapping(CorsConstants.MAPPING_PATTERN)
                .allowedOriginPatterns(patterns)
                .allowedMethods(CorsConstants.ALLOWED_METHODS)
                .allowedHeaders(CorsConstants.ALLOWED_HEADERS)
                .exposedHeaders(CorsConstants.EXPOSED_HEADERS)
                .allowCredentials(true)
                .maxAge(CorsConstants.MAX_AGE_SECONDS);
    }
}