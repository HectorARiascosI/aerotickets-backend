package com.aerotickets.sim;

import java.time.LocalTime;
import java.util.Random;

public class DisruptionEngine {

    public static class Disruption {
        public final boolean diverted;
        public final boolean emergency;
        public final Integer extraDelay;
        public Disruption(boolean d, boolean e, Integer x) {
            this.diverted = d; this.emergency = e; this.extraDelay = x;
        }
    }

    public Disruption compute(LocalTime depTime, int weatherDelay, String aircraftType, int distanceKm, int baseDelaySeed) {
        Random rnd = new Random(baseDelaySeed);

        boolean peak = (depTime.getHour() >= 6 && depTime.getHour() <= 9)
                || (depTime.getHour() >= 17 && depTime.getHour() <= 20);
        int congestion = peak ? (2 + rnd.nextInt(6)) : rnd.nextInt(3); // antes hasta 12 / 5

        int typeBias = (aircraftType.startsWith("ATR") ? 1 : 0);
        int distBias = distanceKm > 900 ? 2 : (distanceKm > 500 ? 1 : 0);

        int extra = congestion + typeBias + distBias;

        boolean diverted = rnd.nextDouble() < (0.005 + weatherDelay / 800.0);
        boolean emergency = rnd.nextDouble() < 0.0015;

        return new Disruption(diverted, emergency, extra);
    }
}