package com.aerotickets.sim;

import java.time.LocalTime;
import java.util.*;

/** Catálogo de aeropuertos Colombia con metadata usada por restricciones/UX. */
public final class AirportCatalogCO {
    private AirportCatalogCO() {}

    public static final class Airport {
        public final String iata;
        public final String icao;
        public final String city;
        public final String name;
        public final String state;
        public final double latitude;
        public final double longitude;
        public final int elevationFt;
        public final int runwayLenM;
        public final String terrain;
        public final List<String> allowedFamilies;

        // ✅ Campos adicionales esperados por otras clases
        public final List<String> bannedFamilies;     // p.ej. jets en EOH
        public final boolean hasCurfew;               // ¿toque de queda?
        public final LocalTime curfewStartLocal;      // hora local inicio
        public final LocalTime curfewEndLocal;        // hora local fin
        public final Integer crosswindLimitKts;       // límite típico de viento cruzado
        public final boolean ils;                     // ¿tiene ILS?

        public Airport(String iata, String icao, String city, String name, String state,
                       double lat, double lon, int elevFt, int runwayM, String terrain, List<String> families,
                       List<String> banned, boolean hasCurfew, LocalTime curfewStart, LocalTime curfewEnd,
                       Integer crosswindLimitKts, boolean ils) {
            this.iata = iata; this.icao = icao; this.city = city; this.name = name; this.state = state;
            this.latitude = lat; this.longitude = lon; this.elevationFt = elevFt; this.runwayLenM = runwayM;
            this.terrain = terrain; this.allowedFamilies = families;
            this.bannedFamilies = banned != null ? List.copyOf(banned) : List.of();
            this.hasCurfew = hasCurfew;
            this.curfewStartLocal = curfewStart;
            this.curfewEndLocal = curfewEnd;
            this.crosswindLimitKts = crosswindLimitKts;
            this.ils = ils;
        }
    }

    private static final Map<String, Airport> A = new HashMap<>();

    private static void put(String iata, String icao, String city, String name, String state,
                            double lat, double lon, int elevFt, int runwayM, String terrain, List<String> fam,
                            List<String> banned, boolean hasCurfew, LocalTime curfewStart, LocalTime curfewEnd,
                            Integer crosswindLimitKts, boolean ils) {
        A.put(iata, new Airport(iata, icao, city, name, state, lat, lon, elevFt, runwayM,
                terrain, fam, banned, hasCurfew, curfewStart, curfewEnd, crosswindLimitKts, ils));
    }

    /** Sobrecarga con defaults seguros (sin toque de queda, ILS sí en grandes). */
    private static void put(String iata, String icao, String city, String name, String state,
                            double lat, double lon, int elevFt, int runwayM, String terrain, List<String> fam) {
        boolean major = Set.of("BOG","MDE","CTG","BAQ","CLO","PEI","AXM","BGA","CUC","ADZ","LET").contains(iata);
        int xwind = major ? 35 : 25;
        boolean hasIls = major || Set.of("BAQ","CTG","CLO","MDE","BOG").contains(iata);
        List<String> banned = List.of();
        boolean curfew = false;
        LocalTime start = null, end = null;

        // Ejemplo: EOH con restricciones a jets + curfew nocturno
        if ("EOH".equals(iata)) {
            banned = List.of("A320-200","A320neo","A321","B737-800","B737");
            xwind = 20;
            hasIls = false;
            curfew = true;
            start = LocalTime.of(22, 0);
            end   = LocalTime.of(6, 0);
        }

        put(iata, icao, city, name, state, lat, lon, elevFt, runwayM, terrain, fam, banned, curfew, start, end, xwind, hasIls);
    }

    static {
        put("BOG","SKBO","Bogotá","El Dorado","Cundinamarca",4.7016,-74.1469,8361,3800,"Llano/Altiplano", List.of("A320-200","A320neo","A319","A321","B737-800","B787","A330"));
        put("MDE","SKRG","Rionegro (Medellín)","José María Córdova","Antioquia",6.1645,-75.4231,6954,3500,"Montañoso", List.of("A320-200","A320neo","A319","A321","B737-800"));
        put("EOH","SKMD","Medellín","Olaya Herrera","Antioquia",6.219,-75.589,4949,2500,"Urbano", List.of("ATR 72-600","ATR 42-600","ERJ-145"));
        put("CTG","SKCG","Cartagena","Rafael Núñez","Bolívar",10.4424,-75.513,6,2600,"Costero", List.of("A320-200","A320neo","A319","B737-800"));
        put("BAQ","SKBQ","Barranquilla","Ernesto Cortissoz","Atlántico",10.8896,-74.7808,94,3000,"Costero", List.of("A320-200","A320neo","A319","B737-800"));
        put("SMR","SKSM","Santa Marta","Simón Bolívar","Magdalena",11.1196,-74.2306,22,1900,"Costero", List.of("A320-200","A320neo","B737-800","ATR 72-600"));
        put("CLO","SKCL","Cali","Alfonso Bonilla Aragón","Valle del Cauca",3.543,-76.3802,3162,3000,"Valle", List.of("A320-200","A320neo","A321","B737-800"));
        put("PEI","SKPE","Pereira","Matecaña","Risaralda",4.8132,-75.7395,4413,2080,"Ondulado", List.of("A320-200","A319","ATR 72-600"));
        put("AXM","SKAR","Armenia","El Edén","Quindío",4.4528,-75.7664,3999,2050,"Ondulado", List.of("A320-200","A319","ATR 72-600"));
        put("BGA","SKBG","Bucaramanga","Palonegro","Santander",7.1265,-73.1848,3897,3000,"Montañoso", List.of("A320-200","A319","B737-800"));
        put("CUC","SKCC","Cúcuta","Camilo Daza","Norte de Santander",7.9286,-72.5115,1096,2300,"Valle", List.of("A320-200","A319","B737-800"));
        put("ADZ","SKSP","San Andrés","Gustavo Rojas Pinilla","San Andrés",12.5836,-81.7112,19,2380,"Isla", List.of("A320-200","A320neo","B737-800"));
        put("LET","SKLT","Leticia","Alfredo Vásquez Cobo","Amazonas",-4.1936,-69.9432,277,2500,"Selva", List.of("A320-200","A319","ATR 72-600"));
        put("IBE","SKIB","Ibagué","Perales","Tolima",4.4216,-75.1333,2995,1800,"Montañoso", List.of("ATR 72-600","ERJ-145"));
        put("RCH","SKRH","Riohacha","Almirante Padilla","La Guajira",11.5262,-72.926,43,2100,"Costero", List.of("A320-200","ATR 72-600"));
        put("NVA","SKNV","Neiva","Benito Salas","Huila",2.9514,-75.2936,1460,1880,"Valle", List.of("ATR 72-600","ERJ-145"));
        put("MZL","SKMZ","Manizales","La Nubia","Caldas",5.0296,-75.4647,6890,1480,"Montañoso", List.of("ATR 42-600","ERJ-145"));
        put("VUP","SKVP","Valledupar","Alfonso López Pumarejo","Cesar",10.435,-73.2495,482,2100,"Valle", List.of("A320-200","ATR 72-600"));
        put("UIB","SKUI","Quibdó","El Caraño","Chocó",5.6908,-76.6412,204,1800,"Selva", List.of("ATR 72-600","ERJ-145"));
        put("PPN","SKPP","Popayán","Guillermo León Valencia","Cauca",2.4544,-76.6093,5689,1850,"Montañoso", List.of("ATR 72-600","ERJ-145"));
        put("GPI","SKGP","Guapi","Juan Casiano Solís","Cauca",2.5712,-77.8986,164,1600,"Selva", List.of("ATR 42-600","ERJ-145"));
        put("EYP","SKYP","Yopal","El Alcaraván","Casanare",5.3191,-72.384,1020,2300,"Llano", List.of("A320-200","ATR 72-600"));
        put("MTR","SKMR","Montería","Los Garzones","Córdoba",8.8237,-75.8258,36,2300,"Valle", List.of("A320-200","A320neo","B737-800"));
        put("VVC","SKVV","Villavicencio","La Vanguardia","Meta",4.168,-73.6138,1399,1800,"Llano", List.of("ATR 72-600","ERJ-145"));
        put("CZU","SKCZ","Corozal (Sincelejo)","Las Brujas","Sucre",9.33275,-75.2856,512,1600,"Valle", List.of("ATR 72-600","ERJ-145"));
        put("PSO","SKPS","Pasto","Antonio Nariño","Nariño",1.3962,-77.2915,5951,2300,"Montañoso", List.of("A320-200","ATR 72-600"));
        put("RVE","SKSA","Saravena","Los Colonizadores","Arauca",6.9556,-71.8572,700,1800,"Llano", List.of("ATR 42-600","ERJ-145"));
        put("APO","SKLC","Carepa (Apartadó)","Antonio Roldán Betancourt","Antioquia",7.81,-76.716,46,1800,"Selva", List.of("ATR 72-600","ERJ-145"));
    }

    public static Airport get(String iata) { return A.get(iata); }
    public static Set<String> keys() { return Collections.unmodifiableSet(A.keySet()); }
    public static boolean isDomesticPair(String o, String d) { return A.containsKey(o) && A.containsKey(d) && !o.equals(d); }
}