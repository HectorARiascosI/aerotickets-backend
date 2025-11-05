package com.aerotickets.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolver de IATA a partir de entrada de usuario amigable.
 * Soporta:
 *  - "BOG"
 *  - "Bogotá"
 *  - "Bogotá - El Dorado (BOG), Colombia"
 *  - Evita tratar strings parciales "bo" como IATA hasta que haya 3 letras válidas.
 */
public class IataResolver {

    private static final Pattern PAREN_IATA = Pattern.compile("\\(([A-Za-z]{3})\\)");

    /** Normaliza acentos y minúsculas para comparar ciudades. */
    public static String normalize(String s) {
        if (s == null) return null;
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return n.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Extrae IATA de una cadena como "Ciudad - Aeropuerto (XXX), País".
     */
    public static String extractIataFromParentheses(String text) {
        if (text == null) return null;
        Matcher m = PAREN_IATA.matcher(text);
        if (m.find()) {
            return m.group(1).toUpperCase(Locale.ROOT);
        }
        return null;
    }

    /**
     * Determina IATA final para consulta: primero intenta extraer (XXX),
     * luego valida si es IATA puro (3 letras). Si no, retornará null hasta
     * que el usuario haya seleccionado una sugerencia válida (con IATA).
     */
    public static String toIata(String input) {
        if (input == null || input.isBlank()) return null;

        // Si viene desde Autocomplete: "Ciudad - Aeropuerto (XXX), País"
        String extracted = extractIataFromParentheses(input);
        if (extracted != null) return extracted;

        String t = input.trim();
        // Si el usuario tecleó exactamente 3 letras, podrían ser IATA válidas
        if (t.matches("^[A-Za-z]{3}$")) {
            return t.toUpperCase(Locale.ROOT);
        }

        // Si escribió "bogota", podríamos resolver con catálogo remoto en el future.
        // Por ahora, dejamos que autocomplete sugiera y se seleccione una opción con (IATA).
        return null;
    }
}