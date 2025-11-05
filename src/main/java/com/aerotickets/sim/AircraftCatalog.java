package com.aerotickets.sim;

import java.util.Map;

public class AircraftCatalog {

    public static class Aircraft {
        public final String code;
        public final int capacity;       // asientos
        public final int cruiseKmh;      // velocidad crucero promedio
        public Aircraft(String code, int capacity, int cruiseKmh) {
            this.code = code; this.capacity = capacity; this.cruiseKmh = cruiseKmh;
        }
    }

    public static final Map<String, Aircraft> DATA = Map.of(
            "A321", new Aircraft("A321", 220, 840),
            "A320", new Aircraft("A320", 180, 830),
            "A320neo", new Aircraft("A320neo", 186, 840),
            "B737-800", new Aircraft("B737-800", 186, 842),
            "B737 MAX 8", new Aircraft("B737 MAX 8", 186, 839),
            "E190", new Aircraft("E190", 104, 820),
            "ATR 72-600", new Aircraft("ATR 72-600", 70, 510)
    );

    public static Aircraft any(String code) { return DATA.get(code); }
    public static String[] types() { return DATA.keySet().toArray(new String[0]); }
}