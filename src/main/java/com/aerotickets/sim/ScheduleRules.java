package com.aerotickets.sim;

import java.time.LocalTime;

/** Reglas de horario básicas (puntas operativas). */
public final class ScheduleRules {
    private ScheduleRules(){}

    public static final LocalTime peakStartLocal = LocalTime.of(6, 0);
    public static final LocalTime peakEndLocal   = LocalTime.of(21, 59);
}