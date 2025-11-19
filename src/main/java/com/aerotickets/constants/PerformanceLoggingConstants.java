package com.aerotickets.constants;

public final class PerformanceLoggingConstants {

    private PerformanceLoggingConstants() {
    }

    public static final String ANSI_RESET = "\033[0m";
    public static final String ANSI_RED = "\033[0;31m";
    public static final String ANSI_GREEN = "\033[0;32m";
    public static final String ANSI_YELLOW = "\033[0;33m";

    public static final String LOG_PATTERN_REQUEST = "%s[HTTP] %s %s - %d (%d ms)%s";
    public static final String LOG_SLOW_REQUEST = "Peticion lenta detectada: {} {} ({} ms)";

    public static final long SLOW_REQUEST_THRESHOLD_MS = 1000L;
}