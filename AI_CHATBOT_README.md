# 🤖 AeroBot - Asistente de IA para AeroTickets

## Descripción

AeroBot es un asistente virtual inteligente integrado en AeroTickets que utiliza la API de Groq (Llama 3.1 70B) para ayudar a los usuarios a buscar vuelos, gestionar reservas y obtener información sobre la plataforma mediante lenguaje natural.

## 🚀 Características Principales

### 1. Búsqueda Inteligente de Vuelos
- Procesamiento de lenguaje natural para extraer origen, destino y fecha
- Reconocimiento de ciudades colombianas y sus códigos IATA
- Interpretación de fechas relativas ("mañana", "pasado mañana", "15/12")
- Búsqueda automática y presentación de resultados

### 2. Gestión de Reservas
- Consulta de reservas del usuario autenticado
- Información sobre estado de reservas y vuelos
- Navegación directa a "Mis Reservas"

### 3. Asistencia Contextual
- Respuestas específicas sobre funcionalidades de AeroTickets
- Información sobre aeropuertos, rutas y servicios
- Ayuda paso a paso para usar la plataforma

### 4. Restricciones de Seguridad
- **Scope limitado**: Solo responde preguntas relacionadas con vuelos y viajes
- **Filtro de temas**: Rechaza consultas sobre deportes, política, entretenimiento, etc.
- **Respuestas profesionales**: Mantiene el contexto de la aplicación

## 🛠️ Arquitectura Técnica

### Backend (Java + Spring Boot)

#### Componentes Principales:
- **`AIChatService`**: Servicio principal que procesa mensajes y llama a Groq
- **`AIChatController`**: Endpoint REST `/api/ai/chat`
- **DTOs**: `ChatMessageDTO`, `ChatResponseDTO`

#### Flujo de Procesamiento:
1. **Análisis de Intención**: Detecta si el usuario quiere buscar vuelos, ver reservas o pedir ayuda
2. **Extracción de Datos**: Parsea ciudades, fechas y parámetros de búsqueda
3. **Ejecución de Acciones**: Realiza búsquedas o consultas según la intención
4. **Generación de Respuesta**: Llama a Groq con contexto específico
5. **Respuesta Estructurada**: Retorna respuesta + acción + datos

#### System Prompt:
```
Eres un asistente virtual de AeroTickets, una plataforma de reserva de vuelos en Colombia.
Tu nombre es AeroBot y tu función es ayudar a los usuarios a buscar y reservar vuelos.

REGLAS IMPORTANTES:
1. SOLO responde preguntas relacionadas con vuelos, reservas, aeropuertos y viajes.
2. Si te preguntan sobre temas NO relacionados, responde: 'Lo siento, solo puedo ayudarte con temas relacionados con vuelos y reservas en AeroTickets.'
3. Sé amable, conciso y profesional.
4. Usa emojis ocasionalmente para ser más amigable (✈️, 🎫, 🌍).
```

### Frontend (React + TypeScript)

#### Componentes:
- **`AIChatBot`**: Componente principal del chat flotante
- **`aiChatService`**: Servicio para comunicación con backend

#### Características UX:
- **Chat flotante**: Botón pulsante en esquina inferior derecha
- **Interfaz conversacional**: Burbujas de chat con timestamps
- **Indicadores visuales**: Loading states, typing indicators
- **Navegación inteligente**: Redirección automática a resultados
- **Responsive**: Adaptado para móviles y desktop

## 🔧 Configuración

### Variables de Entorno

```yaml
# Backend (application.yml)
groq:
  api:
    key: ${GROQ_API_KEY:}
    url: ${GROQ_API_URL:https://api.groq.com/openai/v1/chat/completions}
```

### Render.com
```bash
# Agregar en Variables de Entorno de Render
GROQ_API_KEY=gsk_tu_api_key_aqui
```

### Obtener API Key de Groq
1. Ir a [console.groq.com](https://console.groq.com)
2. Crear cuenta gratuita
3. Generar API Key
4. Agregar a variables de entorno

## 📝 Ejemplos de Uso

### Búsqueda de Vuelos
```
Usuario: "Quiero volar de Bogotá a Medellín mañana"
AeroBot: "¡Encontré 3 vuelos disponibles de Bogotá a Medellín para mañana! 
         Puedes verlos en la lista de abajo y hacer clic en 'Reservar' para continuar. ✈️"
```

### Consulta de Reservas
```
Usuario: "Muéstrame mis vuelos"
AeroBot: "Tienes 2 reservas activas. Te estoy redirigiendo a la sección 'Mis Reservas' 
         donde puedes ver todos los detalles. 🎫"
```

### Solicitud de Ayuda
```
Usuario: "¿Cómo funciona la plataforma?"
AeroBot: "¡Hola! Te ayudo con AeroTickets:
         ✈️ Buscar vuelos entre ciudades colombianas
         🎫 Reservar con selección de asientos
         💳 Pago seguro con Stripe
         📍 Ver rutas en mapa interactivo
         ¿En qué más puedo ayudarte?"
```

### Filtro de Temas No Relacionados
```
Usuario: "¿Quién ganó el partido de fútbol?"
AeroBot: "Lo siento, solo puedo ayudarte con temas relacionados con vuelos y reservas en AeroTickets."
```

## 🎯 Casos de Uso Avanzados

### 1. Búsqueda con Fechas Flexibles
- "Vuelos baratos a Cartagena la próxima semana"
- "¿Cuándo es más barato volar a Cali?"

### 2. Información Contextual
- "¿Qué aeropuertos están disponibles?"
- "¿Cuánto tiempo de vuelo hay de Bogotá a Medellín?"

### 3. Asistencia en Reservas
- "¿Cómo cancelo mi reserva?"
- "¿Puedo cambiar mi asiento?"

## 🔒 Seguridad y Limitaciones

### Restricciones Implementadas:
- **Scope limitado**: Solo temas de vuelos y viajes
- **Sin datos sensibles**: No procesa información de pago
- **Autenticación**: Respeta permisos de usuario
- **Rate limiting**: Protegido por límites de Groq

### Fallbacks:
- Si Groq no está disponible, usa respuestas predefinidas
- Manejo de errores graceful
- Mensajes de error amigables

## 🚀 Futuras Mejoras

### Funcionalidades Planeadas:
1. **Memoria de conversación**: Recordar contexto de chat
2. **Recomendaciones personalizadas**: Basadas en historial
3. **Integración con calendario**: Sugerir fechas óptimas
4. **Notificaciones proactivas**: Cambios de vuelo, ofertas
5. **Soporte multiidioma**: Inglés, portugués
6. **Voice interface**: Comandos de voz

### Optimizaciones Técnicas:
1. **Caché de respuestas**: Para consultas frecuentes
2. **Streaming responses**: Respuestas en tiempo real
3. **Fine-tuning**: Modelo específico para AeroTickets
4. **Analytics avanzados**: Insights de comportamiento

## 📊 Ciudades Soportadas

El chatbot reconoce las siguientes ciudades colombianas:

- Bogotá (BOG)
- Medellín (MDE)
- Cali (CLO)
- Cartagena (CTG)
- Barranquilla (BAQ)
- Pereira (PEI)
- Bucaramanga (BGA)
- Santa Marta (SMR)
- Cúcuta (CUC)
- Pasto (PSO)

## 🎉 Conclusión

AeroBot representa una innovación significativa en la experiencia de usuario de AeroTickets, proporcionando una interfaz conversacional intuitiva que simplifica la búsqueda y gestión de vuelos. Su implementación con Groq garantiza respuestas inteligentes y contextualmente relevantes, mientras que las restricciones de seguridad aseguran que se mantenga enfocado en su propósito principal.
