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

    @Value("${groq.api.key:}")
    private String groqApiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String groqApiUrl;

    public AIChatService(FlightService flightService,
                         ReservationRepository reservationRepository) {
        this.restTemplate = new RestTemplate();
        this.flightService = flightService;
        this.reservationRepository = reservationRepository;
        this.objectMapper = new ObjectMapper();
    }

    public ChatResponseDTO processMessage(String userMessage, String userEmail) {
        try {
            // Analizar el mensaje del usuario
            Map<String, Object> context = analyzeUserIntent(userMessage, userEmail);
            
            // Generar respuesta con Groq
            String aiResponse = callGroqAPI(userMessage, context);
            
            // Determinar si hay una acción específica
            String action = (String) context.get("action");
            Object data = context.get("data");
            
            return new ChatResponseDTO(aiResponse, action, data);
            
        } catch (Exception e) {
            return new ChatResponseDTO(
                "Lo siento, tuve un problema procesando tu solicitud. ¿Podrías reformular tu pregunta?"
            );
        }
    }

    private Map<String, Object> analyzeUserIntent(String message, String userEmail) {
        Map<String, Object> context = new HashMap<>();
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
                    context.put("error", "No se pudieron buscar vuelos");
                }
            }
        }
        
        // Detectar consulta sobre reservas
        else if (containsReservationIntent(lowerMessage)) {
            if (userEmail != null && !userEmail.isBlank()) {
                List<Reservation> reservations = reservationRepository
                    .findByUser_EmailOrderByCreatedAtDesc(userEmail);
                context.put("action", "reservations");
                context.put("data", reservations);
            }
        }
        
        // Detectar solicitud de ayuda
        else if (containsHelpIntent(lowerMessage)) {
            context.put("action", "help");
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

    private String callGroqAPI(String userMessage, Map<String, Object> context) {
        if (groqApiKey == null || groqApiKey.isBlank()) {
            return generateFallbackResponse(userMessage, context);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqApiKey);

            String systemPrompt = buildSystemPrompt(context);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.1-70b-versatile");
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
            ));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                groqApiUrl,
                HttpMethod.POST,
                request,
                String.class
            );

            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            return jsonResponse.get("choices").get(0).get("message").get("content").asText();

        } catch (Exception e) {
            return generateFallbackResponse(userMessage, context);
        }
    }

    private String buildSystemPrompt(Map<String, Object> context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un asistente virtual de AeroTickets, una plataforma de reserva de vuelos en Colombia. ");
        prompt.append("Tu nombre es AeroBot y tu función es ayudar a los usuarios a buscar y reservar vuelos. ");
        prompt.append("\n\nREGLAS IMPORTANTES:\n");
        prompt.append("1. SOLO responde preguntas relacionadas con vuelos, reservas, aeropuertos y viajes.\n");
        prompt.append("2. Si te preguntan sobre temas NO relacionados (deportes, política, entretenimiento, etc.), ");
        prompt.append("responde: 'Lo siento, solo puedo ayudarte con temas relacionados con vuelos y reservas en AeroTickets.'\n");
        prompt.append("3. Sé amable, conciso y profesional.\n");
        prompt.append("4. Usa emojis ocasionalmente para ser más amigable (✈️, 🎫, 🌍).\n");
        prompt.append("5. Si encuentras vuelos, menciona que el usuario puede verlos y reservarlos.\n");
        prompt.append("\n\nFUNCIONALIDADES DE AEROTICKETS:\n");
        prompt.append("- Búsqueda de vuelos entre ciudades colombianas\n");
        prompt.append("- Reserva de vuelos con selección de asientos\n");
        prompt.append("- Gestión de reservas (ver, cancelar)\n");
        prompt.append("- Pago seguro con Stripe\n");
        prompt.append("- Visualización de rutas en mapa interactivo\n");
        prompt.append("\nCIUDADES DISPONIBLES: Bogotá, Medellín, Cali, Cartagena, Barranquilla, Pereira, Bucaramanga, Santa Marta, Cúcuta, Pasto\n");

        String action = (String) context.get("action");
        if ("search".equals(action)) {
            Object data = context.get("data");
            if (data instanceof List) {
                List<?> flights = (List<?>) data;
                prompt.append("\n\nRESULTADO: Encontré ").append(flights.size()).append(" vuelo(s) disponible(s). ");
                prompt.append("Menciona que puede verlos en la lista y hacer clic en 'Reservar' para continuar.");
            }
        } else if ("reservations".equals(action)) {
            Object data = context.get("data");
            if (data instanceof List) {
                List<?> reservations = (List<?>) data;
                prompt.append("\n\nRESULTADO: El usuario tiene ").append(reservations.size()).append(" reserva(s).");
            }
        }

        return prompt.toString();
    }

    private String generateFallbackResponse(String userMessage, Map<String, Object> context) {
        String action = (String) context.get("action");
        
        if ("search".equals(action)) {
            Object data = context.get("data");
            if (data instanceof List) {
                List<?> flights = (List<?>) data;
                if (flights.isEmpty()) {
                    return "No encontré vuelos disponibles para tu búsqueda. ¿Podrías intentar con otras fechas o ciudades? ✈️";
                }
                return String.format("¡Encontré %d vuelo(s) disponible(s)! Puedes verlos en la lista de abajo y hacer clic en 'Reservar' para continuar. 🎫", flights.size());
            }
        } else if ("reservations".equals(action)) {
            Object data = context.get("data");
            if (data instanceof List) {
                List<?> reservations = (List<?>) data;
                return String.format("Tienes %d reserva(s) activa(s). Puedes verlas en la sección 'Mis Reservas'. ✈️", reservations.size());
            }
        } else if ("help".equals(action)) {
            return "¡Hola! Soy AeroBot, tu asistente de vuelos. Puedo ayudarte a:\n\n" +
                   "✈️ Buscar vuelos (ej: 'Quiero volar de Bogotá a Medellín mañana')\n" +
                   "🎫 Ver tus reservas (ej: 'Muéstrame mis vuelos')\n" +
                   "📍 Información sobre aeropuertos y rutas\n\n" +
                   "¿En qué puedo ayudarte hoy?";
        }
        
        return "¡Hola! Soy AeroBot, tu asistente de vuelos. ¿En qué puedo ayudarte hoy? ✈️";
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
