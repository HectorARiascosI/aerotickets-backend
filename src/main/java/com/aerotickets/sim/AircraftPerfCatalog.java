package com.aerotickets.sim;

import java.util.*;

public final class AircraftPerfCatalog {

    public static final class Perf {
        public final String family;          // "A320", "A319", "E190", "ATR 72-600", ...
        public final int baseToraM;          // Requerimiento TORA a nivel del mar, ISA, MTOW (m) — conservador
        public final int baseLdaM;           // Requerimiento LDA a nivel del mar (m)
        public final int crosswindLimitKts;  // límite típico de operación
        public final int cat;                // 1=regional/turboprop, 2=narrow, 3=wide  (para reglas rápidas)

        public Perf(String family, int baseToraM, int baseLdaM, int crosswindLimitKts, int cat) {
            this.family = family;
            this.baseToraM = baseToraM;
            this.baseLdaM = baseLdaM;
            this.crosswindLimitKts = crosswindLimitKts;
            this.cat = cat;
        }
    }

    private static final Map<String, Perf> DB = new HashMap<>();

    static {
        DB.put("ATR 72-600", new Perf("ATR 72-600", 1300, 1100, 30, 1));
        DB.put("E190",       new Perf("E190",       1800, 1500, 33, 1));
        DB.put("E195",       new Perf("E195",       1900, 1600, 33, 1));
        DB.put("A319",       new Perf("A319",       2000, 1600, 35, 2));
        DB.put("A320",       new Perf("A320",       2200, 1700, 35, 2));
        DB.put("A320neo",    new Perf("A320neo",    2100, 1650, 35, 2));
        DB.put("A321",       new Perf("A321",       2400, 1800, 35, 2));
        DB.put("B737-800",   new Perf("B737-800",   2300, 1750, 35, 2));
        DB.put("B737 MAX 8", new Perf("B737 MAX 8", 2250, 1700, 35, 2));
        // Añade más si los necesitas
    }

    public static Perf get(String family){ return DB.get(family); }
    public static Set<String> families(){ return DB.keySet(); }

    // Corrección por densidad: +7% TORA por cada 1000 m de elevación aprox (conservador);
    // +1% por cada 10°C por encima de ISA.
    public static int correctedTora(String family, int toraBaseM, int elevationFt, int deltaTempC) {
        double elevM = elevationFt * 0.3048;
        double factorElev = 1.0 + (elevM/1000.0)*0.07;
        double factorTemp = 1.0 + Math.max(0, deltaTempC)*0.01;
        return (int)Math.ceil(toraBaseM * factorElev * factorTemp);
    }

    public static int correctedLda(String family, int ldaBaseM, int elevationFt, int deltaTempC) {
        double elevM = elevationFt * 0.3048;
        double factorElev = 1.0 + (elevM/1000.0)*0.05;
        double factorTemp = 1.0 + Math.max(0, deltaTempC)*0.005;
        return (int)Math.ceil(ldaBaseM * factorElev * factorTemp);
    }
}