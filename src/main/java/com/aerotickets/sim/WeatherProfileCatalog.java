package com.aerotickets.sim;

import java.util.HashMap;
import java.util.Map;

/** Perfiles meteorológicos simplificados por aeropuerto. */
public final class WeatherProfileCatalog {
    private WeatherProfileCatalog(){}

    public enum Risk { LOW, MEDIUM, HIGH }

    public static final class WeatherProfile {
        public final Risk fogRisk;
        public final Risk heavyRainRisk;
        public final Risk convectiveRisk;

        public WeatherProfile(Risk fog, Risk rain, Risk conv) {
            this.fogRisk = fog;
            this.heavyRainRisk = rain;
            this.convectiveRisk = conv;
        }
    }

    private static final Map<String, WeatherProfile> P = new HashMap<>();
    static {
        // Valores plausibles: costa (lluvia convectiva media), altiplano (niebla media), selva (lluvia alta), etc.
        put("BOG", Risk.MEDIUM, Risk.MEDIUM, Risk.MEDIUM);
        put("MDE", Risk.MEDIUM, Risk.MEDIUM, Risk.MEDIUM);
        put("CTG", Risk.LOW,    Risk.MEDIUM, Risk.MEDIUM);
        put("BAQ", Risk.LOW,    Risk.MEDIUM, Risk.MEDIUM);
        put("SMR", Risk.LOW,    Risk.MEDIUM, Risk.MEDIUM);
        put("CLO", Risk.MEDIUM, Risk.MEDIUM, Risk.MEDIUM);
        put("ADZ", Risk.LOW,    Risk.MEDIUM, Risk.MEDIUM);
        put("LET", Risk.LOW,    Risk.HIGH,   Risk.MEDIUM);
        put("EOH", Risk.MEDIUM, Risk.MEDIUM, Risk.MEDIUM);
        // default para los demás
    }

    private static void put(String iata, Risk fog, Risk rain, Risk conv) {
        P.put(iata, new WeatherProfile(fog, rain, conv));
    }

    public static WeatherProfile profileOf(String iata) {
        return P.getOrDefault(iata, new WeatherProfile(Risk.MEDIUM, Risk.MEDIUM, Risk.MEDIUM));
    }
}