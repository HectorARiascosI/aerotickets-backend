package com.aerotickets.sim;

import java.time.LocalTime;
import java.util.List;

public final class RestrictionsEngine {
    private RestrictionsEngine(){}

    /** Verifica si un avión puede operar por pista/familias. */
    public static boolean isAircraftAllowed(AirportCatalogCO.Airport ap, String aircraftFamily) {
        if (ap == null || aircraftFamily == null) return false;
        List<String> banned = ap.bannedFamilies;
        if (banned != null && banned.contains(aircraftFamily)) return false;

        // Reglas simples por longitud de pista (m)
        int rwy = ap.runwayLenM;
        return switch (aircraftFamily) {
            case "B737-800","A321"    -> rwy >= 2200;
            case "A320-200","A320neo" -> rwy >= 2000;
            case "A319"                -> rwy >= 1800;
            case "ATR 72-600"          -> rwy >= 1400;
            case "ATR 42-600","ERJ-145"-> rwy >= 1200;
            default -> rwy >= 1500;
        };
    }

    /** Toque de queda local. Si hay curfew y la hora cae dentro, NO opera. */
    public static boolean passesCurfew(AirportCatalogCO.Airport ap, LocalTime localTime) {
        if (!ap.hasCurfew || ap.curfewStartLocal == null || ap.curfewEndLocal == null) return true;
        LocalTime s = ap.curfewStartLocal;
        LocalTime e = ap.curfewEndLocal;
        // Intervalo puede cruzar medianoche
        if (s.isBefore(e)) {
            return localTime.isBefore(s) || localTime.isAfter(e);
        } else {
            // 22:00–06:00, por ejemplo
            return localTime.isAfter(e) && localTime.isBefore(s);
        }
    }

    /** Límite de viento cruzado. Si no hay dato, se aprueba. */
    public static boolean meetsCrosswindLimit(AirportCatalogCO.Airport ap, int crosswindKts) {
        if (ap.crosswindLimitKts == null) return true;
        return crosswindKts <= ap.crosswindLimitKts;
    }

    /** ¿Cuenta con ILS? Útil para condiciones IMC. */
    public static boolean hasIls(AirportCatalogCO.Airport ap) {
        return ap.ils;
    }
}