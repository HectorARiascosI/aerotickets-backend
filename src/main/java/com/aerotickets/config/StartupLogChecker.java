package com.aerotickets.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * Verifica si existen errores previos en el último log al iniciar la app.
 * Muestra una alerta visual en consola al arrancar.
 */
@Component
public class StartupLogChecker {

    private static final Logger logger = LoggerFactory.getLogger(StartupLogChecker.class);
    private static final String LOG_DIR = "logs";
    private static final String ERROR_FILE_PREFIX = "errors-Aerotickets";

    @PostConstruct
    public void checkPreviousErrors() {
        try {
            File logDir = new File(LOG_DIR);
            if (!logDir.exists()) return;

            File[] logs = logDir.listFiles((dir, name) -> name.startsWith(ERROR_FILE_PREFIX) && name.endsWith(".log"));
            if (logs == null || logs.length == 0) {
                System.out.println("\033[0;32m✅ No se detectaron errores previos en los registros.\033[0m");
                return;
            }

            File latest = null;
            for (File f : logs) {
                if (latest == null || f.lastModified() > latest.lastModified()) {
                    latest = f;
                }
            }

            if (latest != null && latest.exists()) {
                List<String> lines = Files.readAllLines(latest.toPath());
                if (!lines.isEmpty()) {
                    System.out.println("\033[0;33m⚠️  Se detectaron errores previos en: " + latest.getName() + "\033[0m");
                    System.out.println("\033[0;31mÚltimo error:\033[0m");
                    System.out.println(lines.get(lines.size() - 1));
                } else {
                    System.out.println("\033[0;32m✅ No se detectaron errores previos en los registros.\033[0m");
                }
            }
        } catch (Exception e) {
            logger.warn("No se pudo verificar los logs anteriores: {}", e.getMessage());
        }
    }
}