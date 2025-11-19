package com.aerotickets.constants;

public final class StartupLogConstants {

    private StartupLogConstants() {
    }

    public static final String LOG_DIR = "logs";
    public static final String ERROR_FILE_PREFIX = "errors-Aerotickets";

    public static final String MSG_NO_PREVIOUS_ERRORS = "No se detectaron errores previos en los registros.";
    public static final String MSG_PREVIOUS_ERRORS_DETECTED = "Se detectaron errores previos en: ";
    public static final String MSG_LAST_ERROR_LABEL = "Último error:";
    public static final String MSG_UNABLE_TO_CHECK_LOGS = "No se pudo verificar los logs anteriores: {}";
}