package com.aerotickets.config;

import com.aerotickets.constants.FlywayConstants;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.exception.FlywayValidateException;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy baselineIfNeededThenMigrate() {
        return flyway -> {
            try {
                MigrationInfoService info = flyway.info();

                boolean hasApplied = info != null && info.applied() != null && info.applied().length > 0;
                boolean hasPending = info != null && info.pending() != null && info.pending().length > 0;

                if (FlywayConstants.BASELINE_ON_PENDING_WITHOUT_APPLIED && !hasApplied && hasPending) {
                    flyway.baseline();
                }

                flyway.migrate();
            } catch (FlywayValidateException ex) {
                if (FlywayConstants.BASELINE_ON_VALIDATE_EXCEPTION) {
                    flyway.baseline();
                    flyway.migrate();
                } else {
                    throw ex;
                }
            }
        };
    }
}