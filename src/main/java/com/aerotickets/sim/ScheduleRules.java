package com.aerotickets.sim;

import java.time.LocalTime;

public final class ScheduleRules {

    public static int congestionDelayMin(AirportCatalogCO.Airport ap, LocalTime local) {
        int h = local.getHour();
        boolean peak = h >= ap.peakStartLocal && h < ap.peakEndLocal;
        return peak ? 10 : 2; // base minutos por congestión local
    }
}