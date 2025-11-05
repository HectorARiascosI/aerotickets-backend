package com.aerotickets.sim;

import java.time.LocalTime;

public final class RestrictionsEngine {

    public static final class Decision {
        public final boolean allowed;
        public final String reason;

        public Decision(boolean allowed, String reason) {
            this.allowed = allowed;
            this.reason = reason;
        }
        public static Decision ok(){ return new Decision(true, "OK"); }
        public static Decision no(String why){ return new Decision(false, why); }
    }

    public static Decision canOperate(AirportCatalogCO.Airport ap, String family,
                                      int tempC, WeatherProfileCatalog.Wx wx, LocalTime depLocal) {

        // 1) Familia prohibida o no permitida
        if (ap.bannedFamilies.contains(family)) return Decision.no("Tipo prohibido por aeropuerto");
        if (!ap.allowedFamilies.contains(family)) return Decision.no("Tipo no listado como permitido");

        // 2) Curfew
        if (ap.hasCurfew) {
            int h = depLocal.getHour();
            boolean inCurfew = ap.curfewStartLocal <= ap.curfewEndLocal
                    ? (h >= ap.curfewStartLocal && h < ap.curfewEndLocal)
                    : (h >= ap.curfewStartLocal || h < ap.curfewEndLocal);
            if (inCurfew) return Decision.no("Curfew activo");
        }

        // 3) Viento cruzado
        int limit = Math.min(ap.crosswindLimitKts, AircraftPerfCatalog.get(family).crosswindLimitKts);
        if (wx.crosswindKts > limit) return Decision.no("Viento cruzado supera límite");

        // 4) Performance pista con densidad
        var perf = AircraftPerfCatalog.get(family);
        if (perf == null) return Decision.no("Sin performance definida");

        int deltaTemp = Math.max(0, tempC - 15); // ISA simplificado
        int reqTora = AircraftPerfCatalog.correctedTora(family, perf.baseToraM, ap.elevationFt, deltaTemp);
        int reqLda  = AircraftPerfCatalog.correctedLda(family, perf.baseLdaM, ap.elevationFt, deltaTemp);

        if (ap.runwayLenM < reqTora) return Decision.no("TORA insuficiente");
        if (ap.runwayLenM < reqLda)  return Decision.no("LDA insuficiente");

        // 5) ILS/niebla mínima muy conservadora
        if (wx.fog && !ap.ils) return Decision.no("Niebla sin ILS");

        return Decision.ok();
    }
}