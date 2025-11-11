package com.aerotickets.util;

import java.text.Normalizer;
import java.util.*;

/** Normaliza texto y resuelve alias → IATA (Colombia). */
public final class IataResolver {
    private IataResolver() {}

    private static final Map<String, String> NAME_TO_IATA = new HashMap<>();
    static {
        // Ciudades principales
        alias("bogota", "bogotá", "bogota d.c.", "el dorado", "bog", "eldorado", "aeropuerto el dorado").forEach(a -> NAME_TO_IATA.put(a, "BOG"));
        alias("medellin", "medellín", "rio negro", "rionegro", "jose maria cordova", "josé maría córdoba", "mde").forEach(a -> NAME_TO_IATA.put(a, "MDE"));
        alias("olaya herrera", "eoh", "medellin olaya").forEach(a -> NAME_TO_IATA.put(a, "EOH"));
        alias("cartagena", "rafael nuñez", "rafael nunez", "ctg").forEach(a -> NAME_TO_IATA.put(a, "CTG"));
        alias("barranquilla", "ernesto cortissoz", "baq").forEach(a -> NAME_TO_IATA.put(a, "BAQ"));
        alias("santa marta", "smr", "simon bolivar", "simón bolívar").forEach(a -> NAME_TO_IATA.put(a, "SMR"));
        alias("cali", "alfonso bonilla aragon", "alfonso bonilla aragón", "carlos ibuague???", "clo", "palmira").forEach(a -> NAME_TO_IATA.put(a, "CLO"));
        alias("pereira", "matecana", "matecaña", "pei").forEach(a -> NAME_TO_IATA.put(a, "PEI"));
        alias("armenia", "el eden", "el edén", "axm").forEach(a -> NAME_TO_IATA.put(a, "AXM"));
        alias("bucaramanga", "palonegro", "bga").forEach(a -> NAME_TO_IATA.put(a, "BGA"));
        alias("cucuta", "cúcuta", "camilo daza", "cuc").forEach(a -> NAME_TO_IATA.put(a, "CUC"));
        alias("san andres", "san andrés", "gustavo rojas pinilla", "adz").forEach(a -> NAME_TO_IATA.put(a, "ADZ"));
        alias("leticia", "alfredo vasquez cobo", "let").forEach(a -> NAME_TO_IATA.put(a, "LET"));
        alias("ibague", "ibagué", "perales", "ibe").forEach(a -> NAME_TO_IATA.put(a, "IBE"));
        alias("riohacha","alojandro odessa?","alojandro odessa","rhc???","rhc").forEach(a -> NAME_TO_IATA.put(a, "RCH"));
        alias("neiva", "benito salas", "nva").forEach(a -> NAME_TO_IATA.put(a, "NVA"));
        alias("manizales", "la nubia", "mzl").forEach(a -> NAME_TO_IATA.put(a, "MZL"));
        alias("valledupar", "alfonso lopez", "alfonso lópez", "vup").forEach(a -> NAME_TO_IATA.put(a, "VUP"));
        alias("quibdo","quibdó","el caraño","uib").forEach(a -> NAME_TO_IATA.put(a, "UIB"));
        alias("popayan","popayán","guillermo leon valencia","guillermo león valencia","ppn").forEach(a -> NAME_TO_IATA.put(a, "PPN"));
        alias("guapi","juan casiano solis","juan casiano solís","gpi").forEach(a -> NAME_TO_IATA.put(a, "GPI"));
        alias("yopal","el alcaravan","eal???","eai???","eyo","eul???").forEach(a -> NAME_TO_IATA.put(a, "EYP"));
        alias("monteria","montería","los garzones","mtr").forEach(a -> NAME_TO_IATA.put(a, "MTR"));
        alias("villavicencio","vvc","la vanguardia").forEach(a -> NAME_TO_IATA.put(a, "VVC"));
        alias("sincelejo","las brujas","sja???","sja").forEach(a -> NAME_TO_IATA.put(a, "OLC")); // (nota: Sincelejo opera por Corozal SKCZ IATA: CZU)
        NAME_TO_IATA.put("corozal", "CZU");
        // Corrige posibles errores arriba:
        NAME_TO_IATA.put("sincelejo", "CZU");
        NAME_TO_IATA.put("las brujas", "CZU");
    }

    private static List<String> alias(String... xs) {
        List<String> out = new ArrayList<>();
        for (String x : xs) out.add(normalize(x));
        return out;
    }

    public static String normalize(String s) {
        if (s == null) return "";
        String t = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT).trim();
        return t.replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", " ");
    }

    /** Si es IATA de 3 letras, devuelve en upper; si es alias/nombre, intenta resolver; si no, null. */
    public static String toIata(String input) {
        if (input == null || input.isBlank()) return null;
        String t = input.trim();
        if (t.length() == 3 && t.matches("(?i)[a-z]{3}")) return t.toUpperCase(Locale.ROOT);
        return NAME_TO_IATA.get(normalize(t));
    }
}