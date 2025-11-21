package com.aerotickets.constants;

/**
 * @deprecated Use {@link ConfigKeys.Environment} and {@link LogMessages.Startup} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class AppMessages {

    private AppMessages() {
    }

    public static final String ENV_PROFILE_KEY = ConfigKeys.Environment.PROFILE_KEY;
    public static final String ENV_DB_URL_KEY = ConfigKeys.Environment.DB_URL_KEY;
    public static final String ENV_PORT_KEY = ConfigKeys.Environment.PORT_KEY;

    public static final String DEFAULT_PROFILE = ConfigKeys.Environment.DEFAULT_PROFILE;
    public static final String DEFAULT_DB_URL = ConfigKeys.Environment.DEFAULT_DB_URL;
    public static final String DEFAULT_PORT = ConfigKeys.Environment.DEFAULT_PORT;

    public static final String BANNER_HEADER_SEPARATOR = LogMessages.Startup.BANNER_HEADER;
    public static final String BANNER_FOOTER_SEPARATOR = LogMessages.Startup.BANNER_FOOTER;
    public static final String BANNER_TITLE = LogMessages.Startup.BANNER_TITLE;
    public static final String BANNER_ENV_PREFIX = LogMessages.Startup.BANNER_ENV_PREFIX;
    public static final String BANNER_PORT_PREFIX = LogMessages.Startup.BANNER_PORT_PREFIX;
    public static final String BANNER_DB_URL_PREFIX = LogMessages.Startup.BANNER_DB_URL_PREFIX;
    public static final String BANNER_LOGS_LINE = LogMessages.Startup.BANNER_LOGS_LINE;

    public static final String ENDPOINTS_HEADER = LogMessages.Startup.ENDPOINTS_HEADER;
    public static final String ENDPOINTS_FOOTER = LogMessages.Startup.ENDPOINTS_FOOTER;
    public static final String ENDPOINTS_ERROR_PREFIX = LogMessages.Startup.ENDPOINTS_ERROR_PREFIX;

    public static final String MASK_DB_URL_FALLBACK = "N/A";
    public static final String DB_PASSWORD_REGEX = "(?i)(password=)[^&]+";
    public static final String DB_PASSWORD_REPLACEMENT = "$1********";

    public static final String BRACKET_OPEN = "[";
    public static final String BRACKET_CLOSE_SPACE = "] ";
    public static final String ARROW = " -> ";
}