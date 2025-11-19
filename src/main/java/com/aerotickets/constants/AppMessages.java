package com.aerotickets.constants;

public final class AppMessages {

    private AppMessages() {
    }

    public static final String ENV_PROFILE_KEY = "SPRING_PROFILES_ACTIVE";
    public static final String ENV_DB_URL_KEY = "DB_URL";
    public static final String ENV_PORT_KEY = "PORT";

    public static final String DEFAULT_PROFILE = "dev";
    public static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/aerotickets";
    public static final String DEFAULT_PORT = "8080";

    public static final String BANNER_HEADER_SEPARATOR = "\n==============================================";
    public static final String BANNER_FOOTER_SEPARATOR = "==============================================\n";
    public static final String BANNER_TITLE = " Aerotickets Backend iniciado";
    public static final String BANNER_ENV_PREFIX = " Entorno: ";
    public static final String BANNER_PORT_PREFIX = " Puerto : ";
    public static final String BANNER_DB_URL_PREFIX = " DB URL : ";
    public static final String BANNER_LOGS_LINE = " Logs   : logs/aerotickets.log";

    public static final String ENDPOINTS_HEADER = "========= ENDPOINTS REGISTRADOS =========";
    public static final String ENDPOINTS_FOOTER = "=========================================";
    public static final String ENDPOINTS_ERROR_PREFIX = "No se pudo obtener la lista de endpoints: ";

    public static final String MASK_DB_URL_FALLBACK = "N/A";
    public static final String DB_PASSWORD_REGEX = "(?i)(password=)[^&]+";
    public static final String DB_PASSWORD_REPLACEMENT = "$1********";

    public static final String BRACKET_OPEN = "[";
    public static final String BRACKET_CLOSE_SPACE = "] ";
    public static final String ARROW = " -> ";
}