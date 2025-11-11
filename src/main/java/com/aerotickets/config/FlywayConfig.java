package com.aerotickets.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.exception.FlywayValidateException;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy baselineIfNeededThenMigrate() {
        return (Flyway flyway) -> {
            try {
                // Si no hay tabla de metadatos, info() sigue funcionando y pending() > 0
                MigrationInfoService info = flyway.info();

                boolean hasApplied = info != null && info.applied() != null && info.applied().length > 0;
                boolean hasPending = info != null && info.pending() != null && info.pending().length > 0;

                // Si no hay aplicadas pero sí pendientes, probablemente la BD tiene tablas “huérfanas”
                // y Flyway aún no está inicializado: hacemos baseline a la versión 1.
                if (!hasApplied && hasPending) {
                    flyway.baseline();
                }

                flyway.migrate();
            } catch (FlywayValidateException ex) {
                // Si hay un desajuste, intenta “baseline” y luego migrate
                flyway.baseline();
                flyway.migrate();
            }
        };
    }
}