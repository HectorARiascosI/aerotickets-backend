package com.aerotickets.sim;

import java.time.LocalDate;
import java.util.*;

public final class WeatherProfileCatalog {

    public static final class Wx {
        public final int crosswindKts;
        public final boolean fog;
        public final boolean heavyRain;

        public Wx(int crosswindKts, boolean fog, boolean heavyRain) {
            this.crosswindKts = crosswindKts;
            this.fog = fog;
            this.heavyRain = heavyRain;
        }
    }

    public static Wx forAirportDay(AirportCatalogCO.Airport ap, LocalDate date, Random rnd) {
        int seasonBoostConv =
                (date.getMonthValue() >= 3 && date.getMonthValue() <= 5) ||
                (date.getMonthValue() >= 9 && date.getMonthValue() <= 11) ? 10 : 0;

        int convProb = clamp(ap.convectiveRisk + seasonBoostConv + rnd.nextInt(14) - 7, 0, 100);
        int fogProb  = clamp(ap.fogRisk + rnd.nextInt(12) - 6, 0, 100);
        int rainProb = clamp(ap.heavyRainRisk + rnd.nextInt(12) - 6, 0, 100);

        // Objetivo: heavy ~10–15%, fog ~10–15% en promedio (dependiendo del aeropuerto)
        boolean fog   = rnd.nextInt(100) < Math.min(15, fogProb);
        boolean heavy = rnd.nextInt(100) < Math.min(15, Math.max(convProb, rainProb));

        // Viento cruzado con media más baja
        int base = heavy ? 16 : 9;
        int crosswind = Math.max(0, (int)Math.round(rnd.nextGaussian()*4 + base));
        return new Wx(crosswind, fog, heavy);
    }

    private static int clamp(int v, int lo, int hi){ return Math.max(lo, Math.min(hi, v)); }
}