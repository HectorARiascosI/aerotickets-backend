package com.aerotickets.sim;

import java.util.*;

public final class AirportCatalogCO {

    public static final class Airport {
        public final String iata;
        public final String icao;
        public final String name;
        public final String city;
        public final double lat;
        public final double lon;

        // Infraestructura/entorno
        public final int runwayLenM;
        public final int runwayWidthM;
        public final int elevationFt;
        public final String surface;
        public final boolean ils;
        public final boolean rnpAr;
        public final String terrain;

        // Operación
        public final Set<String> allowedFamilies;
        public final Set<String> bannedFamilies;
        public final Set<String> allowedCarriers; // AV, LA, 9R, VE, P5 (sin Ultra/Viva)
        public final boolean hasCurfew;
        public final int curfewStartLocal; // hora local [0..23] inclusivo
        public final int curfewEndLocal;   // hora local [0..23] exclusivo
        public final int peakStartLocal;   // inicio hora pico
        public final int peakEndLocal;     // fin hora pico

        // Clima (riesgos base relativos 0..100)
        public final int crosswindLimitKts;
        public final int fogRisk;
        public final int convectiveRisk;
        public final int heavyRainRisk;

        private Airport(Builder b) {
            this.iata = b.iata;
            this.icao = b.icao;
            this.name = b.name;
            this.city = b.city;
            this.lat = b.lat;
            this.lon = b.lon;
            this.runwayLenM = b.runwayLenM;
            this.runwayWidthM = b.runwayWidthM;
            this.elevationFt = b.elevationFt;
            this.surface = b.surface;
            this.ils = b.ils;
            this.rnpAr = b.rnpAr;
            this.terrain = b.terrain;
            this.allowedFamilies = Set.copyOf(b.allowedFamilies);
            this.bannedFamilies = Set.copyOf(b.bannedFamilies);
            this.allowedCarriers = Set.copyOf(b.allowedCarriers);
            this.hasCurfew = b.hasCurfew;
            this.curfewStartLocal = b.curfewStartLocal;
            this.curfewEndLocal = b.curfewEndLocal;
            this.peakStartLocal = b.peakStartLocal;
            this.peakEndLocal = b.peakEndLocal;
            this.crosswindLimitKts = b.crosswindLimitKts;
            this.fogRisk = b.fogRisk;
            this.convectiveRisk = b.convectiveRisk;
            this.heavyRainRisk = b.heavyRainRisk;
        }

        public static class Builder {
            private String iata, icao, name, city, surface, terrain;
            private double lat, lon;
            private int runwayLenM, runwayWidthM, elevationFt;
            private boolean ils, rnpAr;
            private final Set<String> allowedFamilies = new LinkedHashSet<>();
            private final Set<String> bannedFamilies = new LinkedHashSet<>();
            private final Set<String> allowedCarriers = new LinkedHashSet<>();
            private boolean hasCurfew;
            private int curfewStartLocal, curfewEndLocal, peakStartLocal, peakEndLocal;
            private int crosswindLimitKts = 25, fogRisk = 10, convectiveRisk = 20, heavyRainRisk = 20;

            public Builder iata(String v){ this.iata=v; return this; }
            public Builder icao(String v){ this.icao=v; return this; }
            public Builder name(String v){ this.name=v; return this; }
            public Builder city(String v){ this.city=v; return this; }
            public Builder lat(double v){ this.lat=v; return this; }
            public Builder lon(double v){ this.lon=v; return this; }
            public Builder runwayLenM(int v){ this.runwayLenM=v; return this; }
            public Builder runwayWidthM(int v){ this.runwayWidthM=v; return this; }
            public Builder elevationFt(int v){ this.elevationFt=v; return this; }
            public Builder surface(String v){ this.surface=v; return this; }
            public Builder ils(boolean v){ this.ils=v; return this; }
            public Builder rnpAr(boolean v){ this.rnpAr=v; return this; }
            public Builder terrain(String v){ this.terrain=v; return this; }
            public Builder allow(String... fam){ this.allowedFamilies.addAll(Arrays.asList(fam)); return this; }
            public Builder ban(String... fam){ this.bannedFamilies.addAll(Arrays.asList(fam)); return this; }
            public Builder carriers(String... carr){ this.allowedCarriers.addAll(Arrays.asList(carr)); return this; }
            public Builder curfew(boolean has, int start, int end){ this.hasCurfew=has; this.curfewStartLocal=start; this.curfewEndLocal=end; return this; }
            public Builder peaks(int start, int end){ this.peakStartLocal=start; this.peakEndLocal=end; return this; }
            public Builder met(int crosswind, int fog, int conv, int rain){ this.crosswindLimitKts=crosswind; this.fogRisk=fog; this.convectiveRisk=conv; this.heavyRainRisk=rain; return this; }
            public Airport build(){ return new Airport(this); }
        }
    }

    private static final Map<String, Airport> DB = new LinkedHashMap<>();

    static {
        // === HUBS PRINCIPALES ===
        DB.put("BOG", new Airport.Builder()
                .iata("BOG").icao("SKBO").name("El Dorado Intl.").city("Bogotá")
                .lat(4.70159).lon(-74.1469)
                .runwayLenM(3800).runwayWidthM(45).elevationFt(8361)
                .surface("ASPHALT").ils(true).rnpAr(true).terrain("PLAINS")
                .allow("A321","A320","A320neo","A319","B737-800","B737 MAX 8","E190","ATR 72-600")
                .carriers("AV","LA","9R","VE","P5")
                .curfew(false,0,0).peaks(6,10)
                .met(28,10,30,40)
                .build());

        DB.put("MDE", new Airport.Builder()
                .iata("MDE").icao("SKRG").name("José María Córdova Intl.").city("Rionegro/Medellín")
                .lat(6.16454).lon(-75.4231)
                .runwayLenM(3470).runwayWidthM(45).elevationFt(6950)
                .surface("ASPHALT").ils(true).rnpAr(true).terrain("MOUNTAINOUS")
                .allow("A321","A320","A320neo","A319","B737-800","B737 MAX 8","E190","ATR 72-600")
                .carriers("AV","LA","9R","VE","P5")
                .curfew(false,0,0).peaks(6,10)
                .met(26,15,40,50)
                .build());

        DB.put("CLO", new Airport.Builder()
                .iata("CLO").icao("SKCL").name("Alfonso Bonilla Aragón Intl.").city("Cali")
                .lat(3.54322).lon(-76.3816)
                .runwayLenM(3000).runwayWidthM(45).elevationFt(3162)
                .surface("ASPHALT").ils(true).rnpAr(true).terrain("PLAINS")
                .allow("A321","A320","A320neo","A319","B737-800","B737 MAX 8","E190","ATR 72-600")
                .carriers("AV","LA","9R","VE","P5")
                .curfew(false,0,0).peaks(6,10)
                .met(28,10,35,45)
                .build());

        // === COSTA CARIBE / NORTE ===
        DB.put("CTG", new Airport.Builder()
                .iata("CTG").icao("SKCG").name("Rafael Núñez Intl.").city("Cartagena")
                .lat(10.442).lon(-75.513)
                .runwayLenM(2600).runwayWidthM(45).elevationFt(4)
                .surface("ASPHALT").ils(true).rnpAr(true).terrain("COASTAL")
                .allow("A320","A319","B737-800","B737 MAX 8","E190","ATR 72-600")
                .carriers("AV","LA","VE","P5")
                .curfew(false,0,0).peaks(7,11)
                .met(28,5,30,50)
                .build());

        DB.put("SMR", new Airport.Builder()
                .iata("SMR").icao("SKSM").name("Simón Bolívar Intl.").city("Santa Marta")
                .lat(11.1196).lon(-74.2306)
                .runwayLenM(1700).runwayWidthM(45).elevationFt(23)
                .surface("ASPHALT").ils(false).rnpAr(true).terrain("COASTAL")
                .allow("A319","A320","E190","ATR 72-600")
                .carriers("AV","VE","P5")
                .curfew(false,0,0).peaks(8,12)
                .met(25,5,25,60)
                .build());

        DB.put("BAQ", new Airport.Builder()
                .iata("BAQ").icao("SKBQ").name("Ernesto Cortissoz Intl.").city("Barranquilla")
                .lat(10.8896).lon(-74.7808)
                .runwayLenM(3000).runwayWidthM(45).elevationFt(98)
                .surface("ASPHALT").ils(true).rnpAr(true).terrain("COASTAL")
                .allow("A321","A320","A320neo","A319","B737-800","B737 MAX 8","E190")
                .carriers("AV","LA","VE","P5")
                .curfew(false,0,0).peaks(7,11)
                .met(27,5,30,55)
                .build());

        DB.put("RCH", new Airport.Builder()
                .iata("RCH").icao("SKRH").name("Almirante Padilla").city("Riohacha")
                .lat(11.5262).lon(-72.9260)
                .runwayLenM(2100).runwayWidthM(45).elevationFt(43)
                .surface("ASPHALT").ils(false).rnpAr(true).terrain("COASTAL")
                .allow("A320","A319","E190","ATR 72-600","B737-800")
                .carriers("AV","VE","P5")
                .curfew(false,0,0).peaks(8,12)
                .met(24,5,25,55)
                .build());

        DB.put("ADZ", new Airport.Builder()
                .iata("ADZ").icao("SKSP").name("Gustavo Rojas Pinilla Intl.").city("San Andrés")
                .lat(12.5836).lon(-81.7112)
                .runwayLenM(2380).runwayWidthM(45).elevationFt(19)
                .surface("ASPHALT").ils(false).rnpAr(true).terrain("COASTAL")
                .allow("A320","A319","E190","ATR 72-600","B737-800")
                .ban("A321","B737 MAX 8")
                .carriers("AV","LA","P5","VE")
                .curfew(false,0,0).peaks(9,13)
                .met(25,5,25,60)
                .build());

        // === ANDINA / ORIENTE ===
        DB.put("BGA", new Airport.Builder()
                .iata("BGA").icao("SKBG").name("Palonegro Intl.").city("Bucaramanga")
                .lat(7.1265).lon(-73.1848)
                .runwayLenM(2600).runwayWidthM(45).elevationFt(3900)
                .surface("ASPHALT").ils(true).rnpAr(true).terrain("MOUNTAINOUS")
                .allow("A320","A319","E190","ATR 72-600")
                .carriers("AV","LA","9R","VE")
                .curfew(false,0,0).peaks(6,9)
                .met(25,15,35,50)
                .build());

        DB.put("CUC", new Airport.Builder()
                .iata("CUC").icao("SKCC").name("Camilo Daza Intl.").city("Cúcuta")
                .lat(7.92757).lon(-72.5115)
                .runwayLenM(2100).runwayWidthM(45).elevationFt(1096)
                .surface("ASPHALT").ils(true).rnpAr(true).terrain("VALLEY")
                .allow("A319","A320","E190","ATR 72-600")
                .carriers("AV","LA","9R","VE")
                .curfew(false,0,0).peaks(7,10)
                .met(24,10,30,45)
                .build());

        DB.put("LET", new Airport.Builder()
                .iata("LET").icao("SKLT").name("Alfredo Vásquez Cobo Intl.").city("Leticia")
                .lat(-4.19355).lon(-69.9432)
                .runwayLenM(2100).runwayWidthM(45).elevationFt(277)
                .surface("ASPHALT").ils(false).rnpAr(true).terrain("RAINFOREST")
                .allow("A320","A319","E190","ATR 72-600")
                .carriers("AV","9R")
                .curfew(false,0,0).peaks(9,12)
                .met(20,20,40,60)
                .build());

        // === PACÍFICO / SUROCCIDENTE (incluye Pasto que ya tenías) ===
        DB.put("PSO", new Airport.Builder()
                .iata("PSO").icao("SKPS").name("Antonio Nariño").city("Pasto")
                .lat(1.39625).lon(-77.2915)
                .runwayLenM(2300).runwayWidthM(45).elevationFt(5951)
                .surface("ASPHALT").ils(false).rnpAr(true).terrain("MOUNTAINOUS")
                .allow("A320","A319","E190","ATR 72-600")
                .ban("A321","B737-800","B737 MAX 8")
                .carriers("AV","LA","9R","VE")
                .curfew(false,0,0).peaks(7,10)
                .met(20,20,35,50)
                .build());

        DB.put("PPN", new Airport.Builder()
                .iata("PPN").icao("SKPP").name("Guillermo León Valencia").city("Popayán")
                .lat(2.4544).lon(-76.6093)
                .runwayLenM(1850).runwayWidthM(30).elevationFt(5687)
                .surface("ASPHALT").ils(false).rnpAr(true).terrain("MOUNTAINOUS")
                .allow("ATR 72-600","E190")
                .ban("A321","A320","A319","B737-800","B737 MAX 8")
                .carriers("9R","VE","AV")
                .curfew(false,0,0).peaks(7,10)
                .met(22,20,30,50)
                .build());

        DB.put("NVA", new Airport.Builder()
                .iata("NVA").icao("SKNV").name("Benito Salas").city("Neiva")
                .lat(2.95015).lon(-75.2940)
                .runwayLenM(1860).runwayWidthM(30).elevationFt(1467)
                .surface("ASPHALT").ils(false).rnpAr(true).terrain("VALLEY")
                .allow("A319","E190","ATR 72-600")
                .ban("A321","A320","B737-800","B737 MAX 8")
                .carriers("AV","9R","VE")
                .curfew(false,0,0).peaks(7,10)
                .met(24,10,30,45)
                .build());

        // === EJE CAFETERO ===
        DB.put("PEI", new Airport.Builder()
                .iata("PEI").icao("SKPE").name("Matecaña").city("Pereira")
                .lat(4.81267).lon(-75.7395)
                .runwayLenM(2040).runwayWidthM(45).elevationFt(4411)
                .surface("ASPHALT").ils(true).rnpAr(true).terrain("MOUNTAINOUS")
                .allow("A320","A319","E190","ATR 72-600","B737-800")
                .ban("A321","B737 MAX 8")
                .carriers("AV","LA","9R","VE")
                .curfew(false,0,0).peaks(6,9)
                .met(24,15,35,45)
                .build());

        DB.put("AXM", new Airport.Builder()
                .iata("AXM").icao("SKAR").name("El Edén").city("Armenia")
                .lat(4.45278).lon(-75.7664)
                .runwayLenM(2100).runwayWidthM(45).elevationFt(3990)
                .surface("ASPHALT").ils(true).rnpAr(true).terrain("MOUNTAINOUS")
                .allow("A320","A319","E190","ATR 72-600")
                .ban("A321","B737 MAX 8")
                .carriers("AV","LA","9R","VE")
                .curfew(false,0,0).peaks(6,9)
                .met(24,15,35,45)
                .build());

        DB.put("MZL", new Airport.Builder()
                .iata("MZL").icao("SKMZ").name("La Nubia").city("Manizales")
                .lat(5.0296).lon(-75.4647)
                .runwayLenM(1480).runwayWidthM(30).elevationFt(6890)
                .surface("ASPHALT").ils(false).rnpAr(true).terrain("MOUNTAINOUS")
                .allow("ATR 72-600") // históricamente turboprop; limita jets
                .ban("E190","A319","A320","A321","B737-800","B737 MAX 8")
                .carriers("9R","VE")
                .curfew(false,0,0).peaks(7,10)
                .met(20,25,35,50)
                .build());

        // === LLANOS / NORTE / CHOCÓ / CÓRDOBA ===
        DB.put("VVC", new Airport.Builder()
                .iata("VVC").icao("SKVV").name("Vanguardia").city("Villavicencio")
                .lat(4.16787).lon(-73.6138)
                .runwayLenM(2000).runwayWidthM(45).elevationFt(1394)
                .surface("ASPHALT").ils(false).rnpAr(true).terrain("PLAINS")
                .allow("E190","ATR 72-600") // comercial regional; también opera carga/legacy
                .ban("A321","A320","A319","B737-800","B737 MAX 8")
                .carriers("9R","VE")
                .curfew(false,0,0).peaks(7,10)
                .met(22,10,35,55)
                .build());

        DB.put("UIB", new Airport.Builder()
                .iata("UIB").icao("SKUI").name("El Caraño").city("Quibdó")
                .lat(5.69076).lon(-76.6412)
                .runwayLenM(1800).runwayWidthM(30).elevationFt(204)
                .surface("ASPHALT").ils(false).rnpAr(true).terrain("RAINFOREST")
                .allow("ATR 72-600","E190")
                .ban("A321","A320","A319","B737-800","B737 MAX 8")
                .carriers("9R","VE","AV")
                .curfew(false,0,0).peaks(8,11)
                .met(22,15,45,70)
                .build());

        DB.put("MTR", new Airport.Builder()
                .iata("MTR").icao("SKMR").name("Los Garzones").city("Montería")
                .lat(8.8237).lon(-75.8258)
                .runwayLenM(2400).runwayWidthM(45).elevationFt(36)
                .surface("ASPHALT").ils(true).rnpAr(true).terrain("PLAINS")
                .allow("A321","A320","A320neo","A319","E190","B737-800")
                .ban("B737 MAX 8")
                .carriers("AV","LA","VE","P5")
                .curfew(false,0,0).peaks(7,10)
                .met(26,8,35,60)
                .build());

        // === SECUNDARIO DE MEDELLÍN (SOLO REGIONAL/TURBOPROP) ===
        DB.put("EOH", new Airport.Builder()
                .iata("EOH").icao("SKMD").name("Olaya Herrera").city("Medellín")
                .lat(6.21956).lon(-75.5906)
                .runwayLenM(2500).runwayWidthM(45).elevationFt(4927)
                .surface("ASPHALT").ils(false).rnpAr(true).terrain("MOUNTAINOUS")
                .allow("ATR 72-600","E190") // operación principal regional
                .ban("A321","A320","A319","B737-800","B737 MAX 8")
                .carriers("9R","VE")
                .curfew(false,0,0).peaks(7,10)
                .met(22,15,35,45)
                .build());
    }

    public static Set<String> keys(){ return DB.keySet(); }
    public static Airport get(String iata){ return DB.get(iata); }
}