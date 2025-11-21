package com.aerotickets.service;

import com.aerotickets.dto.ChatResponseDTO;
import com.aerotickets.dto.FlightSearchDTO;
import com.aerotickets.entity.Flight;
import com.aerotickets.entity.Reservation;
import com.aerotickets.repository.ReservationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIChatService {

    private final RestTemplate restTemplate;
    private final FlightService flightService;
    private final ReservationRepository reservationRepository;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;

    public AIChatService(FlightService flightService,
                         ReservationRepository reservationRepository) {
        this.restTemplate = new RestTemplate();
        this.flightService = flightService;
        this.reservationRepository = reservationRepository;
        this.objectMapper = new ObjectMapper();
    }

    public ChatResponseDTO processMessage(String userMessage, String userEmail) {
        try {
            if (userMessage == null || userMessage.trim().isEmpty()) {
                return new ChatResponseDTO("Por favor envía un mensaje válido.");
            }
            
            // Analizar el mensaje del usuario
            Map<String, Object> context = analyzeUserIntent(userMessage, userEmail);
            
            // Generar respuesta con OpenAI
            String aiResponse = callOpenAI(userMessage, context);
            
            // Determinar si hay una acción específica
            String action = (String) context.get("action");
            Object data = context.get("data");
            
            return new ChatResponseDTO(aiResponse, action, data);
            
        } catch (Exception e) {
            // Log del error para debugging
            System.err.println("Error en AIChatService.processMessage: " + e.getMessage());
            e.printStackTrace();
            
            return new ChatResponseDTO(
                "Lo siento, tuve un problema procesando tu solicitud. ¿Podrías reformular tu pregunta?"
            );
        }
    }

    private Map<String, Object> analyzeUserIntent(String message, String userEmail) {
        Map<String, Object> context = new HashMap<>();
        
        try {
            String lowerMessage = message.toLowerCase();

            // Detectar intención de búsqueda de vuelos
            if (containsFlightSearchIntent(lowerMessage)) {
                FlightSearchInfo searchInfo = extractFlightSearchInfo(message);
                if (searchInfo.isValid()) {
                    try {
                        FlightSearchDTO searchDTO = new FlightSearchDTO();
                        searchDTO.setOrigin(searchInfo.origin);
                        searchDTO.setDestination(searchInfo.destination);
                        searchDTO.setDate(searchInfo.date);
                        
                        List<Flight> flights = flightService.searchOrSimulate(searchDTO);
                        
                        context.put("action", "search");
                        context.put("data", flights);
                        context.put("searchInfo", searchInfo);
                    } catch (Exception e) {
                        System.err.println("Error buscando vuelos: " + e.getMessage());
                        context.put("error", "No se pudieron buscar vuelos");
                    }
                }
            }
            
            // Detectar consulta sobre reservas
            else if (containsReservationIntent(lowerMessage)) {
                if (userEmail != null && !userEmail.isBlank()) {
                    try {
                        List<Reservation> reservations = reservationRepository
                            .findByUser_EmailOrderByCreatedAtDesc(userEmail);
                        context.put("action", "reservations");
                        context.put("data", reservations);
                    } catch (Exception e) {
                        System.err.println("Error obteniendo reservas: " + e.getMessage());
                    }
                }
            }
            
            // Detectar solicitud de ayuda
            else if (containsHelpIntent(lowerMessage)) {
                context.put("action", "help");
            }
        } catch (Exception e) {
            System.err.println("Error en analyzeUserIntent: " + e.getMessage());
            e.printStackTrace();
        }

        return context;
    }

    private boolean containsFlightSearchIntent(String message) {
        String[] keywords = {"vuelo", "volar", "buscar", "reservar", "viaje", "ir a", "viajar"};
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsReservationIntent(String message) {
        String[] keywords = {"mis vuelos", "mis reservas", "reservas", "compras", "tickets"};
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsHelpIntent(String message) {
        String[] keywords = {"ayuda", "help", "cómo", "como", "qué puedes", "que puedes"};
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private FlightSearchInfo extractFlightSearchInfo(String message) {
        FlightSearchInfo info = new FlightSearchInfo();
        
        // Códigos IATA de aeropuertos colombianos
        Map<String, String> cities = Map.ofEntries(
            Map.entry("bogotá", "BOG"),
            Map.entry("bogota", "BOG"),
            Map.entry("medellín", "MDE"),
            Map.entry("medellin", "MDE"),
            Map.entry("cali", "CLO"),
            Map.entry("cartagena", "CTG"),
            Map.entry("barranquilla", "BAQ"),
            Map.entry("pereira", "PEI"),
            Map.entry("bucaramanga", "BGA"),
            Map.entry("santa marta", "SMR"),
            Map.entry("cúcuta", "CUC"),
            Map.entry("cucuta", "CUC"),
            Map.entry("pasto", "PSO")
        );
        
        String lowerMessage = message.toLowerCase();
        
        // Extraer origen y destino
        for (Map.Entry<String, String> entry : cities.entrySet()) {
            if (lowerMessage.contains(entry.getKey())) {
                if (info.origin == null) {
                    info.origin = entry.getValue();
                } else if (info.destination == null) {
                    info.destination = entry.getValue();
                }
            }
        }
        
        // Extraer fecha
        info.date = extractDate(lowerMessage);
        
        return info;
    }

    private LocalDate extractDate(String message) {
        LocalDate today = LocalDate.now();
        
        // Palabras clave temporales
        if (message.contains("hoy")) {
            return today;
        } else if (message.contains("mañana")) {
            return today.plusDays(1);
        } else if (message.contains("pasado mañana")) {
            return today.plusDays(2);
        }
        
        // Buscar fechas en formato dd/mm o dd-mm
        Pattern datePattern = Pattern.compile("(\\d{1,2})[/-](\\d{1,2})");
        Matcher matcher = datePattern.matcher(message);
        if (matcher.find()) {
            try {
                int day = Integer.parseInt(matcher.group(1));
                int month = Integer.parseInt(matcher.group(2));
                int year = today.getYear();
                LocalDate date = LocalDate.of(year, month, day);
                if (date.isBefore(today)) {
                    date = date.plusYears(1);
                }
                return date;
            } catch (Exception e) {
                // Ignorar errores de parsing
            }
        }
        
        return today;
    }

    private String callOpenAI(String userMessage, Map<String, Object> context) {
        if (openaiApiKey == null || openaiApiKey.isBlank()) {
            return generateFallbackResponse(userMessage, context);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            String systemPrompt = buildSystemPrompt(context);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
            ));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 300);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                openaiApiUrl,
                HttpMethod.POST,
                request,
                String.class
            );

            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            return jsonResponse.get("choices").get(0).get("message").get("content").asText();

        } catch (Exception e) {
            System.err.println("Error llamando a OpenAI: " + e.getMessage());
            return generateFallbackResponse(userMessage, context);
        }
    }

    private String buildSystemPrompt(Map<String, Object> context) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Eres AeroBot, asistente virtual profesional de AeroTickets, plataforma de reserva de vuelos en Colombia.\n\n");
        
        prompt.append("IDENTIDAD Y COMPORTAMIENTO:\n");
        prompt.append("- Eres experto en vuelos, reservas y viajes dentro de Colombia\n");
        prompt.append("- Respondes SOLO sobre temas relacionados con AeroTickets\n");
        prompt.append("- Si preguntan temas externos (deportes, política, etc.), educadamente redirige al tema de vuelos\n");
        prompt.append("- Tono: Profesional, amigable y conciso\n");
        prompt.append("- Usa emojis moderadamente (✈️, 🎫, 💺)\n");
        prompt.append("- Respuestas máximo 3-4 líneas\n\n");
        
        prompt.append("FUNCIONALIDADES DE AEROTICKETS:\n");
        prompt.append("1. Búsqueda de vuelos: origen, destino, fecha\n");
        prompt.append("2. Reserva de vuelos con selección de asientos (formato: 1A, 2B, 12F)\n");
        prompt.append("3. Gestión de reservas: ver, cancelar, pagar\n");
        prompt.append("4. Pago seguro con Stripe\n");
        prompt.append("5. Visualización de rutas en mapa interactivo\n");
        prompt.append("6. Limpiar historial de reservas antiguas\n\n");
        
        prompt.append("CIUDADES DISPONIBLES:\n");
        prompt.append("Bogotá (BOG), Medellín (MDE), Cali (CLO), Cartagena (CTG), Barranquilla (BAQ), ");
        prompt.append("Pereira (PEI), Bucaramanga (BGA), Santa Marta (SMR), Cúcuta (CUC), Pasto (PSO)\n\n");
        
        prompt.append("REGLAS IMPORTANTES:\n");
        prompt.append("- Un usuario solo puede reservar un vuelo específico una vez\n");
        prompt.append("- Los asientos se asignan automáticamente si no se elige uno\n");
        prompt.append("- Las reservas se pueden cancelar antes del vuelo\n");
        prompt.append("- El pago se realiza después de reservar\n\n");

        String action = (String) context.get("action");
        if ("search".equals(action)) {
            Object data = context.get("data");
            if (data instanceof List) {
                List<?> flights = (List<?>) data;
                prompt.append("CONTEXTO ACTUAL: Encontré ").append(flights.size()).append(" vuelo(s) disponible(s). ");
                prompt.append("Informa al usuario y menciona que puede verlos en la lista para reservar.\n");
            }
        } else if ("reservations".equals(action)) {
            Object data = context.get("data");
            if (data instanceof List) {
                List<?> reservations = (List<?>) data;
                prompt.append("CONTEXTO ACTUAL: El usuario tiene ").append(reservations.size()).append(" reserva(s). ");
                prompt.append("Menciona que lo estás redirigiendo a 'Mis Reservas'.\n");
            }
        } else if ("help".equals(action)) {
            prompt.append("CONTEXTO ACTUAL: Usuario solicita ayuda. Explica las funcionalidades principales brevemente.\n");
        }

        return prompt.toString();
    }

    private String generateFallbackResponse(String userMessage, Map<String, Object> context) {
        String action = (String) context.get("action");
        String lowerMessage = userMessage.toLowerCase();
        
        // Detectar temas no relacionados
        String[] bannedTopics = {"fútbol", "futbol", "partido", "gol", "política", "politica", 
                                 "elecciones", "presidente", "película", "pelicula", "serie", 
                                 "música", "musica", "canción", "clima", "tiempo", "temperatura",
                                 "receta", "comida", "cocinar", "juego", "videojuego"};
        
        for (String topic : bannedTopics) {
            if (lowerMessage.contains(topic)) {
                return "Lo siento, solo puedo ayudarte con temas relacionados con vuelos y reservas en AeroTickets. ¿Necesitas buscar un vuelo o gestionar tus reservas? ✈️";
            }
        }
        
        if ("search".equals(action)) {
            Object data = context.get("data");
            if (data instanceof List) {
                List<?> flights = (List<?>) data;
                if (flights.isEmpty()) {
                    return "No encontré vuelos disponibles para tu búsqueda. 😔\n\n" +
                           "Intenta con:\n" +
                           "• Otras fechas cercanas\n" +
                           "• Ciudades alternativas\n" +
                           "• Verificar que las ciudades estén disponibles\n\n" +
                           "¿Quieres intentar otra búsqueda?";
                }
                return String.format("¡Excelente! Encontré %d vuelo(s) disponible(s) para ti. 🎫\n\n" +
                                   "Puedes verlos en la lista de abajo. Haz clic en 'Reservar' en el vuelo que prefieras para continuar con tu reserva.", 
                                   flights.size());
            }
        } else if ("reservations".equals(action)) {
            Object data = context.get("data");
            if (data instanceof List) {
                List<?> reservations = (List<?>) data;
                if (reservations.isEmpty()) {
                    return "No tienes reservas activas en este momento. 📋\n\n" +
                           "¿Te gustaría buscar un vuelo? Dime origen, destino y fecha. ✈️";
                }
                return String.format("Tienes %d reserva(s) en total. Te estoy redirigiendo a 'Mis Reservas' donde puedes:\n\n" +
                                   "• Ver detalles de cada vuelo\n" +
                                   "• Pagar reservas pendientes\n" +
                                   "• Cancelar si es necesario", 
                                   reservations.size());
            }
        } else if ("help".equals(action)) {
            return "¡Hola! Soy AeroBot, tu asistente personal de vuelos. 👋\n\n" +
                   "Puedo ayudarte con:\n\n" +
                   "✈️ Buscar vuelos entre ciudades colombianas\n" +
                   "🎫 Ver y gestionar tus reservas\n" +
                   "💺 Información sobre asientos y precios\n" +
                   "📍 Detalles de aeropuertos y rutas\n\n" +
                   "Ejemplo: 'Quiero volar de Bogotá a Cartagena mañana'\n\n" +
                   "¿Qué necesitas hoy?";
        }
        
        // Respuesta por defecto mejorada
        if (lowerMessage.contains("hola") || lowerMessage.contains("buenos") || lowerMessage.contains("buenas")) {
            return "¡Hola! Bienvenido a AeroTickets. 👋✈️\n\n" +
                   "Estoy aquí para ayudarte a encontrar el vuelo perfecto.\n\n" +
                   "¿A dónde quieres viajar?";
        }
        
        if (lowerMessage.contains("gracias")) {
            return "¡De nada! Estoy aquí para ayudarte. 😊\n\n" +
                   "¿Necesitas algo más?";
        }
        
        return "Soy AeroBot, tu asistente de vuelos en AeroTickets. ✈️\n\n" +
               "Puedo ayudarte a buscar vuelos, gestionar reservas y responder preguntas sobre la plataforma.\n\n" +
               "¿Qué necesitas?";
    }

    private static class FlightSearchInfo {
        String origin;
        String destination;
        LocalDate date;

        boolean isValid() {
            return origin != null && destination != null && date != null;
        }
    }
}
