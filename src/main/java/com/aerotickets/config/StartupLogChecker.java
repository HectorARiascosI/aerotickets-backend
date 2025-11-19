package com.aerotickets.config;

import com.aerotickets.constants.StartupLogConstants;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Component
public class StartupLogChecker {

    private static final Logger logger = LoggerFactory.getLogger(StartupLogChecker.class);

    @PostConstruct
    public void checkPreviousErrors() {
        try {
            File logDir = new File(StartupLogConstants.LOG_DIR);
            if (!logDir.exists()) {
                return;
            }

            File[] logs = logDir.listFiles((dir, name) ->
                    name.startsWith(StartupLogConstants.ERROR_FILE_PREFIX) && name.endsWith(".log")
            );
            if (logs == null || logs.length == 0) {
                System.out.println(StartupLogConstants.MSG_NO_PREVIOUS_ERRORS);
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
                    System.out.println(StartupLogConstants.MSG_PREVIOUS_ERRORS_DETECTED + latest.getName());
                    System.out.println(StartupLogConstants.MSG_LAST_ERROR_LABEL);
                    System.out.println(lines.get(lines.size() - 1));
                } else {
                    System.out.println(StartupLogConstants.MSG_NO_PREVIOUS_ERRORS);
                }
            }
        } catch (Exception e) {
            logger.warn(StartupLogConstants.MSG_UNABLE_TO_CHECK_LOGS, e.getMessage());
        }
    }
}