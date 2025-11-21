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
            
            // Analizar el mensaje del usuario y ejecutar acciones
            Map<String, Object> context = analyzeUserIntent(userMessage, userEmail);
            
            // Ejecutar acciones automáticas basadas en la intención
            ChatResponseDTO autoResponse = executeAutomaticActions(context, userEmail, userMessage);
            if (autoResponse != null) {
                return autoResponse;
            }
            
            // Si no hay acción automática, generar respuesta inteligente
            String aiResponse = callOpenAI(userMessage, context);
            
            String action = (String) context.get("action");
            Object data = context.get("data");
            
            return new ChatResponseDTO(aiResponse, action, data);
            
        } catch (Exception e) {
            System.err.println("Error en AIChatService.processMessage: " + e.getMessage());
            e.printStackTrace();
            
            return new ChatResponseDTO(
                "Lo siento, tuve un problema procesando tu solicitud. ¿Podrías reformular tu pregunta?"
            );
        }
    }
    
    private ChatResponseDTO executeAutomaticActions(Map<String, Object> context, String userEmail, String userMessage) {
        String action = (String) context.get("action");
        
        // ACCIÓN: BÚSQUEDA AUTOMÁTICA DE VUELOS
        if ("search".equals(action)) {
            Object searchInfoObj = context.get("searchInfo");
            Object data = context.get("data");
            
            if (searchInfoObj != null && data instanceof List) {
                FlightSearchInfo info = (FlightSearchInfo) searchInfoObj;
                List<?> flights = (List<?>) data;
                
                if (!flights.isEmpty()) {
                    // Construir respuesta detallada con los vuelos
                    StringBuilder response = new StringBuilder();
                    response.append(String.format("🎯 Búsqueda completada: %s → %s (%s)\n\n",
                        getCityName(info.origin), getCityName(info.destination), formatDate(info.date)));
                    
                    response.append(String.format("✅ Encontré %d vuelo(s) disponible(s):\n\n", flights.size()));
                    
                    int count = 0;
                    for (Object flightObj : flights) {
                        if (count >= 3) break; // Mostrar máximo 3 vuelos
                        if (flightObj instanceof Flight) {
                            Flight flight = (Flight) flightObj;
                            count++;
                            response.append(String.format("✈️ Vuelo %d:\n", count));
                            response.append(String.format("   Aerolínea: %s\n", flight.getAirline()));
                            response.append(String.format("   Salida: %s\n", formatDateTime(flight.getDepartureAt())));
                            response.append(String.format("   Llegada: %s\n", formatDateTime(flight.getArriveAt())));
                            response.append(String.format("   Precio: $%,.0f COP\n", flight.getPrice()));
                            response.append(String.format("   Asientos: %d disponibles\n\n", flight.getTotalSeats()));
                        }
                    }
                    
                    if (flights.size() > 3) {
                        response.append(String.format("... y %d vuelo(s) más.\n\n", flights.size() - 3));
                    }
                    
                    response.append("💡 Haz clic en 'Reservar' en el vuelo que prefieras para continuar.");
                    
                    return new ChatResponseDTO(response.toString(), "search", flights);
                }
            }
        }
        
        // ACCIÓN: MOSTRAR RESERVAS CON DETALLES
        if ("reservations".equals(action)) {
            Object data = context.get("data");
            if (data instanceof List) {
                List<?> reservations = (List<?>) data;
                
                if (reservations.isEmpty()) {
                    return new ChatResponseDTO(
                        "📋 No tienes reservas activas.\n\n" +
                        "¿Quieres buscar un vuelo? Dime:\n" +
                        "• 'Buscar vuelos de Bogotá a Cali'\n" +
                        "• 'Quiero volar a Cartagena mañana'",
                        "reservations", reservations
                    );
                }
                
                StringBuilder response = new StringBuilder();
                response.append(String.format("📋 Tienes %d reserva(s):\n\n", reservations.size()));
                
                int count = 0;
                for (Object resObj : reservations) {
                    if (count >= 5) break;
                    if (resObj instanceof Reservation) {
                        Reservation res = (Reservation) resObj;
                        count++;
                        response.append(String.format("🎫 Reserva %d:\n", count));
                        response.append(String.format("   Ruta: %s → %s\n", 
                            res.getFlight().getOrigin(), res.getFlight().getDestination()));
                        response.append(String.format("   Fecha: %s\n", formatDateTime(res.getFlight().getDepartureAt())));
                        response.append(String.format("   Asiento: %s\n", res.getSeatNumber()));
                        response.append(String.format("   Estado: %s\n", res.getStatus()));
                        response.append(String.format("   Pagado: %s\n\n", res.getPaid() ? "Sí ✅" : "No ❌"));
                    }
                }
                
                response.append("Te redirijo a 'Mis Reservas' para más opciones...");
                
                return new ChatResponseDTO(response.toString(), "reservations", reservations);
            }
        }
        
        return null; // No hay acción automática
    }
    
    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
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
                                 "música", "musica", "canción", "receta", "comida", "cocinar", "juego", "videojuego"};
        
        for (String topic : bannedTopics) {
            if (lowerMessage.contains(topic)) {
                return "Lo siento, solo puedo ayudarte con temas relacionados con vuelos y reservas en AeroTickets. ¿Necesitas buscar un vuelo o gestionar tus reservas? ✈️";
            }
        }
        
        // BÚSQUEDA DE VUELOS
        if ("search".equals(action)) {
            Object data = context.get("data");
            Object searchInfoObj = context.get("searchInfo");
            
            if (data instanceof List) {
                List<?> flights = (List<?>) data;
                
                if (flights.isEmpty()) {
                    if (searchInfoObj != null) {
                        FlightSearchInfo info = (FlightSearchInfo) searchInfoObj;
                        return String.format("No encontré vuelos disponibles de %s a %s para %s. 😔\n\n" +
                               "Te sugiero:\n" +
                               "• Intentar con fechas cercanas\n" +
                               "• Verificar otras rutas disponibles\n" +
                               "• Consultar vuelos para la próxima semana\n\n" +
                               "¿Quieres que busque en otras fechas?",
                               getCityName(info.origin), getCityName(info.destination), 
                               formatDate(info.date));
                    }
                    return "No encontré vuelos para tu búsqueda. Intenta con otras ciudades o fechas. 😔";
                }
                
                if (searchInfoObj != null) {
                    FlightSearchInfo info = (FlightSearchInfo) searchInfoObj;
                    return String.format("¡Perfecto! Encontré %d vuelo(s) de %s a %s para %s. 🎫\n\n" +
                           "Los vuelos están listos abajo. Haz clic en 'Reservar' en tu favorito para continuar.\n\n" +
                           "💡 Tip: Los precios varían según la aerolínea y horario.",
                           flights.size(), getCityName(info.origin), getCityName(info.destination), 
                           formatDate(info.date));
                }
                
                return String.format("¡Excelente! Encontré %d vuelo(s) disponible(s). 🎫\n\n" +
                       "Revisa las opciones abajo y selecciona el que más te convenga.", flights.size());
            }
        }
        
        // GESTIÓN DE RESERVAS
        else if ("reservations".equals(action)) {
            Object data = context.get("data");
            if (data instanceof List) {
                List<?> reservations = (List<?>) data;
                if (reservations.isEmpty()) {
                    return "No tienes reservas activas en este momento. 📋\n\n" +
                           "¿Quieres buscar un vuelo? Dime:\n" +
                           "• 'Buscar vuelos de Bogotá a Cali'\n" +
                           "• 'Quiero volar a Cartagena mañana'\n" +
                           "• 'Vuelos para el fin de semana'";
                }
                return String.format("Tienes %d reserva(s). Te redirijo a 'Mis Reservas' donde puedes:\n\n" +
                       "✅ Ver detalles completos\n" +
                       "💳 Pagar reservas pendientes\n" +
                       "❌ Cancelar si cambias de planes\n" +
                       "📧 Recibir confirmación por email", reservations.size());
            }
        }
        
        // AYUDA GENERAL
        else if ("help".equals(action)) {
            return "¡Hola! Soy AeroBot, tu asistente inteligente de vuelos. 🤖✈️\n\n" +
                   "Puedo ayudarte con:\n\n" +
                   "🔍 Buscar vuelos: 'Quiero volar de Bogotá a Medellín mañana'\n" +
                   "🎫 Ver reservas: 'Muéstrame mis vuelos'\n" +
                   "❌ Cancelar: 'Cancelar mi reserva'\n" +
                   "💰 Precios: '¿Cuánto cuesta volar a Cartagena?'\n" +
                   "📍 Aeropuertos: '¿Qué ciudades están disponibles?'\n" +
                   "💺 Asientos: 'Información sobre asientos'\n\n" +
                   "¿Qué necesitas?";
        }
        
        // PREGUNTAS SOBRE PRECIOS
        if (lowerMessage.contains("cuánto") || lowerMessage.contains("cuanto") || 
            lowerMessage.contains("precio") || lowerMessage.contains("cuesta") || lowerMessage.contains("costo")) {
            return "Los precios de los vuelos varían según:\n\n" +
                   "📅 Fecha del viaje\n" +
                   "✈️ Aerolínea seleccionada\n" +
                   "⏰ Horario del vuelo\n" +
                   "🎫 Disponibilidad de asientos\n\n" +
                   "Para ver precios exactos, busca tu vuelo con origen, destino y fecha.\n\n" +
                   "Ejemplo: 'Buscar vuelos de Bogotá a Cali para mañana'";
        }
        
        // PREGUNTAS SOBRE CIUDADES/AEROPUERTOS
        if (lowerMessage.contains("ciudades") || lowerMessage.contains("aeropuertos") || 
            lowerMessage.contains("destinos") || lowerMessage.contains("dónde") || lowerMessage.contains("donde")) {
            return "Operamos en 10 ciudades principales de Colombia: 🇨🇴\n\n" +
                   "🏙️ Bogotá (BOG)\n" +
                   "🌆 Medellín (MDE)\n" +
                   "🌴 Cali (CLO)\n" +
                   "🏖️ Cartagena (CTG)\n" +
                   "🌊 Barranquilla (BAQ)\n" +
                   "☕ Pereira (PEI)\n" +
                   "🏔️ Bucaramanga (BGA)\n" +
                   "🌅 Santa Marta (SMR)\n" +
                   "🌄 Cúcuta (CUC)\n" +
                   "⛰️ Pasto (PSO)\n\n" +
                   "¿A dónde quieres viajar?";
        }
        
        // PREGUNTAS SOBRE ASIENTOS
        if (lowerMessage.contains("asiento") || lowerMessage.contains("asientos") || lowerMessage.contains("sentar")) {
            return "Sobre los asientos en AeroTickets: 💺\n\n" +
                   "✅ Puedes elegir tu asiento al reservar (ej: 1A, 12F)\n" +
                   "🎲 O dejar que se asigne automáticamente\n" +
                   "📋 Formato: Número + Letra (1-30, A-F)\n" +
                   "🚫 No puedes reservar asientos ya ocupados\n\n" +
                   "Al reservar, verás un mapa de asientos disponibles.";
        }
        
        // PREGUNTAS SOBRE CANCELACIÓN
        if (lowerMessage.contains("cancelar") || lowerMessage.contains("cancelación") || lowerMessage.contains("devol")) {
            return "Sobre cancelaciones: ❌\n\n" +
                   "✅ Puedes cancelar reservas activas antes del vuelo\n" +
                   "📱 Ve a 'Mis Reservas' y haz clic en 'Cancelar'\n" +
                   "⚠️ La cancelación es irreversible\n" +
                   "💰 Consulta políticas de reembolso con tu aerolínea\n\n" +
                   "¿Necesitas cancelar una reserva ahora?";
        }
        
        // PREGUNTAS SOBRE PAGO
        if (lowerMessage.contains("pagar") || lowerMessage.contains("pago") || lowerMessage.contains("tarjeta") || lowerMessage.contains("stripe")) {
            return "Sobre pagos en AeroTickets: 💳\n\n" +
                   "🔒 Pagos 100% seguros con Stripe\n" +
                   "💳 Aceptamos tarjetas de crédito/débito\n" +
                   "✅ Puedes reservar primero y pagar después\n" +
                   "📧 Recibirás confirmación por email\n" +
                   "🔐 Tus datos están protegidos\n\n" +
                   "Para pagar, ve a 'Mis Reservas' y haz clic en 'Pagar'.";
        }
        
        // SALUDOS
        if (lowerMessage.contains("hola") || lowerMessage.contains("buenos") || lowerMessage.contains("buenas") || 
            lowerMessage.contains("hey") || lowerMessage.contains("saludos")) {
            return "¡Hola! Bienvenido a AeroTickets. 👋✈️\n\n" +
                   "Soy AeroBot, tu asistente inteligente de vuelos.\n\n" +
                   "Puedo ayudarte a:\n" +
                   "• Buscar y reservar vuelos\n" +
                   "• Gestionar tus reservas\n" +
                   "• Responder preguntas sobre viajes\n\n" +
                   "¿A dónde quieres viajar hoy?";
        }
        
        // DESPEDIDAS
        if (lowerMessage.contains("gracias") || lowerMessage.contains("perfecto") || lowerMessage.contains("excelente")) {
            return "¡De nada! Fue un placer ayudarte. 😊✈️\n\n" +
                   "Si necesitas algo más, aquí estaré.\n\n" +
                   "¡Buen viaje! 🌍";
        }
        
        if (lowerMessage.contains("adiós") || lowerMessage.contains("adios") || lowerMessage.contains("chao") || lowerMessage.contains("hasta")) {
            return "¡Hasta pronto! Que tengas un excelente viaje. ✈️😊\n\n" +
                   "Vuelve cuando necesites ayuda con tus vuelos.";
        }
        
        // RESPUESTA POR DEFECTO INTELIGENTE
        return "Soy AeroBot, tu asistente inteligente de vuelos. 🤖✈️\n\n" +
               "Puedo ayudarte con:\n" +
               "• Buscar vuelos: 'Quiero volar a Cartagena'\n" +
               "• Ver reservas: 'Mis vuelos'\n" +
               "• Info de precios, ciudades, asientos y más\n\n" +
               "¿Qué necesitas saber?";
    }
    
    private String getCityName(String code) {
        Map<String, String> cities = Map.of(
            "BOG", "Bogotá", "MDE", "Medellín", "CLO", "Cali",
            "CTG", "Cartagena", "BAQ", "Barranquilla", "PEI", "Pereira",
            "BGA", "Bucaramanga", "SMR", "Santa Marta", "CUC", "Cúcuta", "PSO", "Pasto"
        );
        return cities.getOrDefault(code, code);
    }
    
    private String formatDate(LocalDate date) {
        if (date == null) return "hoy";
        LocalDate today = LocalDate.now();
        if (date.equals(today)) return "hoy";
        if (date.equals(today.plusDays(1))) return "mañana";
        if (date.equals(today.plusDays(2))) return "pasado mañana";
        
        String[] months = {"enero", "febrero", "marzo", "abril", "mayo", "junio",
                          "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
        return date.getDayOfMonth() + " de " + months[date.getMonthValue() - 1];
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
