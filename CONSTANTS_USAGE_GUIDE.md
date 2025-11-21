# Constants Usage Guide - Aerotickets Backend

## Quick Start

### Using New Constants (Recommended)

```java
// Import the new organized constants
import com.aerotickets.constants.ApiPaths;
import com.aerotickets.constants.ErrorMessages;
import com.aerotickets.constants.SuccessMessages;
import com.aerotickets.constants.BusinessRules;

// Use them in your code
@RestController
@RequestMapping(ApiPaths.Auth.BASE)
public class AuthController {
    
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userExists(req.getEmail())) {
            return ResponseEntity.badRequest()
                .body(ErrorMessages.Auth.EMAIL_ALREADY_REGISTERED);
        }
        
        // Business logic...
        
        return ResponseEntity.ok(SuccessMessages.Auth.USER_REGISTERED);
    }
}
```

### Using Old Constants (Still Works)

```java
// Old way still works (backward compatible)
import com.aerotickets.constants.AuthConstants;

@RestController
@RequestMapping(AuthConstants.BASE_PATH)
public class AuthController {
    // Your existing code works without changes
}
```

---

## Constants Reference

### 1. ApiPaths - API Endpoint Routes

**Purpose:** Define all API endpoint paths

```java
import com.aerotickets.constants.ApiPaths;

// Authentication
ApiPaths.Auth.BASE           // "/auth"
ApiPaths.Auth.LOGIN          // "/login"
ApiPaths.Auth.REGISTER       // "/register"
ApiPaths.Auth.FORGOT_PASSWORD // "/forgot-password"
ApiPaths.Auth.RESET_PASSWORD  // "/reset-password"

// Flights
ApiPaths.Flights.BASE        // "/flights"
ApiPaths.Flights.SEARCH      // "/search"

// Reservations
ApiPaths.Reservations.BASE   // "/reservations"
ApiPaths.Reservations.MY     // "/my"
ApiPaths.Reservations.ME     // "/me"

// Payments
ApiPaths.Payments.BASE       // "/payments"
ApiPaths.Payments.CHECKOUT_SESSION // "/checkout-session"
```

**Usage Example:**
```java
@RestController
@RequestMapping(ApiPaths.Flights.BASE)
public class FlightController {
    
    @PostMapping(ApiPaths.Flights.SEARCH)
    public ResponseEntity<List<Flight>> search(@RequestBody FlightSearchDTO dto) {
        // Implementation
    }
}
```

---

### 2. ErrorMessages - Error Messages

**Purpose:** All user-facing error messages

```java
import com.aerotickets.constants.ErrorMessages;

// Authentication Errors
ErrorMessages.Auth.EMAIL_ALREADY_REGISTERED
ErrorMessages.Auth.USER_NOT_FOUND
ErrorMessages.Auth.EMAIL_REQUIRED
ErrorMessages.Auth.INVALID_TOKEN_OR_USER

// Flight Errors
ErrorMessages.Flight.MISSING_REQUIRED_FIELDS
ErrorMessages.Flight.DEPARTURE_IN_PAST
ErrorMessages.Flight.ORIGIN_DEST_REQUIRED

// Reservation Errors
ErrorMessages.Reservation.NO_SEATS_AVAILABLE
ErrorMessages.Reservation.SEAT_TAKEN
ErrorMessages.Reservation.FLIGHT_NOT_FOUND
```

**Usage Example:**
```java
public User register(String email, String password) {
    if (userRepository.existsByEmail(email)) {
        throw new IllegalArgumentException(
            ErrorMessages.Auth.EMAIL_ALREADY_REGISTERED
        );
    }
    // Implementation
}
```

---

### 3. SuccessMessages - Success Messages

**Purpose:** All user-facing success messages

```java
import com.aerotickets.constants.SuccessMessages;

// Authentication Success
SuccessMessages.Auth.USER_REGISTERED
SuccessMessages.Auth.PASSWORD_RESET_SENT
SuccessMessages.Auth.PASSWORD_UPDATED
```

**Usage Example:**
```java
public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    userService.register(req);
    return ResponseEntity.ok(
        Map.of("message", SuccessMessages.Auth.USER_REGISTERED)
    );
}
```

---

### 4. ValidationMessages - Bean Validation

**Purpose:** Messages for Jakarta Bean Validation annotations

```java
import com.aerotickets.constants.ValidationMessages;

// Common Validation
ValidationMessages.Common.NOT_NULL
ValidationMessages.Common.NOT_BLANK

// Email Validation
ValidationMessages.Email.INVALID_FORMAT
ValidationMessages.Email.REQUIRED

// Password Validation
ValidationMessages.Password.MIN_LENGTH
ValidationMessages.Password.REQUIRED
```

**Usage Example:**
```java
public class RegisterRequest {
    
    @NotBlank(message = ValidationMessages.Email.REQUIRED)
    @Email(message = ValidationMessages.Email.INVALID_FORMAT)
    private String email;
    
    @NotBlank(message = ValidationMessages.Password.REQUIRED)
    @Size(min = 8, message = ValidationMessages.Password.MIN_LENGTH)
    private String password;
}
```

---

### 5. LogMessages - Application Logging

**Purpose:** Internal logging messages (not user-facing)

```java
import com.aerotickets.constants.LogMessages;

// Startup Logs
LogMessages.Startup.BANNER_TITLE
LogMessages.Startup.ENDPOINTS_HEADER

// Email Logs
LogMessages.Email.PASSWORD_RESET_SENT
LogMessages.Email.SENDGRID_ERROR

// Security Logs
LogMessages.Security.UNAUTHORIZED_ACCESS_ATTEMPT
LogMessages.Security.JWT_VALIDATION_FAILED
```

**Usage Example:**
```java
@Slf4j
public class EmailService {
    
    public void sendPasswordReset(String email, String url) {
        try {
            // Send email
            log.info(LogMessages.Email.PASSWORD_RESET_SENT, maskEmail(email));
        } catch (Exception e) {
            log.error(LogMessages.Email.PASSWORD_RESET_ERROR, 
                     maskEmail(email), e.getMessage(), e);
        }
    }
}
```

---

### 6. ConfigKeys - Configuration Properties

**Purpose:** Configuration property keys and default values

```java
import com.aerotickets.constants.ConfigKeys;

// Environment Variables
ConfigKeys.Environment.PROFILE_KEY      // "SPRING_PROFILES_ACTIVE"
ConfigKeys.Environment.DB_URL_KEY       // "DB_URL"
ConfigKeys.Environment.DEFAULT_PORT     // "8080"

// CORS Configuration
ConfigKeys.Cors.ALLOWED_ORIGINS_PROPERTY
ConfigKeys.Cors.DEFAULT_ALLOWED_ORIGINS

// Email Configuration
ConfigKeys.Email.DEFAULT_FROM
ConfigKeys.Email.SENDER_NAME

// Cache Configuration
ConfigKeys.Cache.LIVE_FLIGHTS_CACHE_NAME
ConfigKeys.Cache.LIVE_FLIGHTS_MAXIMUM_SIZE
```

**Usage Example:**
```java
@Configuration
public class CacheConfig {
    
    @Bean
    public Cache liveFlightsCache() {
        return Caffeine.newBuilder()
            .maximumSize(ConfigKeys.Cache.LIVE_FLIGHTS_MAXIMUM_SIZE)
            .expireAfterWrite(
                ConfigKeys.Cache.LIVE_FLIGHTS_EXPIRE_AFTER_WRITE_MINUTES,
                TimeUnit.MINUTES
            )
            .build();
    }
}
```

---

### 7. HttpConstants - HTTP-Related Constants

**Purpose:** HTTP headers, methods, content types, CORS settings

```java
import com.aerotickets.constants.HttpConstants;

// Headers
HttpConstants.Headers.AUTHORIZATION
HttpConstants.Headers.CONTENT_TYPE
HttpConstants.Headers.BEARER_PREFIX

// Methods
HttpConstants.Methods.GET
HttpConstants.Methods.POST
HttpConstants.Methods.DELETE

// Content Types
HttpConstants.ContentTypes.APPLICATION_JSON
HttpConstants.ContentTypes.TEXT_PLAIN

// CORS Settings
HttpConstants.Cors.ALLOWED_METHODS
HttpConstants.Cors.ALLOWED_HEADERS
HttpConstants.Cors.MAX_AGE_SECONDS
```

**Usage Example:**
```java
public class JwtAuthFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) {
        String header = request.getHeader(HttpConstants.Headers.AUTHORIZATION);
        
        if (header != null && header.startsWith(HttpConstants.Headers.BEARER_PREFIX)) {
            String token = header.substring(HttpConstants.Headers.BEARER_PREFIX.length());
            // Process token
        }
    }
}
```

---

### 8. SecurityPatterns - Security Patterns

**Purpose:** Security endpoint patterns and ant matchers

```java
import com.aerotickets.constants.SecurityPatterns;

// Endpoint Arrays
SecurityPatterns.PUBLIC_ENDPOINTS
SecurityPatterns.AUTH_ENDPOINTS
SecurityPatterns.LIVE_ENDPOINTS
SecurityPatterns.CATALOG_GET_ENDPOINTS

// Patterns
SecurityPatterns.ANT_PATTERN_ALL
SecurityPatterns.PUBLIC_PREFIXES
```

**Usage Example:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(SecurityPatterns.PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers(SecurityPatterns.AUTH_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.GET, SecurityPatterns.FLIGHTS_GET_ENDPOINTS).permitAll()
                .anyRequest().authenticated()
            )
            .build();
    }
}
```

---

### 9. BusinessRules - Business Logic Constants

**Purpose:** Business rules, default values, calculation constants

```java
import com.aerotickets.constants.BusinessRules;

// Flight Rules
BusinessRules.Flight.DEFAULT_TOTAL_SEATS      // 180
BusinessRules.Flight.DEFAULT_DURATION_HOURS   // 2
BusinessRules.Flight.DEFAULT_PRICE            // BigDecimal.ZERO
BusinessRules.Flight.DEFAULT_AIRLINE_NAME     // "Aerotickets"

// Auth Rules
BusinessRules.Auth.TEMP_TOKEN_MINUTES         // 10
BusinessRules.Auth.MIN_JWT_SECRET_LENGTH      // 32

// Email Templates
BusinessRules.Email.SUBJECT_PASSWORD_RESET
BusinessRules.Email.TEMPLATE_PASSWORD_RESET_TEXT
```

**Usage Example:**
```java
@Service
public class FlightService {
    
    public Flight createFlight(FlightDTO dto) {
        return Flight.builder()
            .airline(dto.getAirline() != null ? 
                    dto.getAirline() : 
                    BusinessRules.Flight.DEFAULT_AIRLINE_NAME)
            .totalSeats(dto.getTotalSeats() != null ? 
                       dto.getTotalSeats() : 
                       BusinessRules.Flight.DEFAULT_TOTAL_SEATS)
            .price(dto.getPrice() != null ? 
                  dto.getPrice() : 
                  BusinessRules.Flight.DEFAULT_PRICE)
            .build();
    }
}
```

---

## Best Practices

### 1. Always Use Constants for Text

❌ **Bad:**
```java
throw new IllegalArgumentException("Email is already registered");
```

✅ **Good:**
```java
throw new IllegalArgumentException(ErrorMessages.Auth.EMAIL_ALREADY_REGISTERED);
```

### 2. Group Related Imports

✅ **Good:**
```java
import com.aerotickets.constants.ApiPaths;
import com.aerotickets.constants.ErrorMessages;
import com.aerotickets.constants.SuccessMessages;
```

### 3. Use Nested Classes for Organization

✅ **Good:**
```java
ErrorMessages.Auth.EMAIL_ALREADY_REGISTERED
ErrorMessages.Flight.DEPARTURE_IN_PAST
ErrorMessages.Reservation.NO_SEATS_AVAILABLE
```

### 4. Prefer New Constants Over Old

⚠️ **Works but deprecated:**
```java
import com.aerotickets.constants.AuthConstants;
AuthConstants.MSG_EMAIL_ALREADY_REGISTERED
```

✅ **Recommended:**
```java
import com.aerotickets.constants.ErrorMessages;
ErrorMessages.Auth.EMAIL_ALREADY_REGISTERED
```

---

## Migration Tips

### Gradual Migration

You don't need to change everything at once:

1. **New Code** - Always use new constants
2. **Bug Fixes** - Update to new constants when fixing bugs
3. **Features** - Update to new constants when adding features
4. **Refactoring** - Update to new constants during refactoring

### Finding Old Constants

If you're using an old constant and want to find the new one:

1. Look at the @Deprecated annotation
2. Check the JavaDoc comment
3. Refer to CONSTANTS_REFACTORING.md
4. Search in the new constant files

### IDE Support

Most IDEs will show deprecation warnings and suggest alternatives:
- IntelliJ IDEA: Shows strikethrough and suggests replacement
- Eclipse: Shows warning marker
- VS Code: Shows deprecation indicator

---

## Common Patterns

### Controller Pattern

```java
import com.aerotickets.constants.ApiPaths;
import com.aerotickets.constants.ErrorMessages;
import com.aerotickets.constants.SuccessMessages;

@RestController
@RequestMapping(ApiPaths.Reservations.BASE)
public class ReservationController {
    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ReservationRequestDTO dto) {
        try {
            Reservation reservation = service.create(dto);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
```

### Service Pattern

```java
import com.aerotickets.constants.ErrorMessages;
import com.aerotickets.constants.BusinessRules;

@Service
public class ReservationService {
    
    public Reservation create(ReservationRequestDTO dto) {
        Flight flight = flightRepository.findById(dto.getFlightId())
            .orElseThrow(() -> new NotFoundException(
                ErrorMessages.Reservation.FLIGHT_NOT_FOUND
            ));
        
        if (flight.getAvailableSeats() <= 0) {
            throw new ConflictException(
                ErrorMessages.Reservation.NO_SEATS_AVAILABLE
            );
        }
        
        // Create reservation
    }
}
```

---

## Summary

- ✅ Use **new constants** for all new code
- ✅ **Old constants still work** (backward compatible)
- ✅ **Migrate gradually** as you work on code
- ✅ Follow **naming conventions** (nested classes)
- ✅ Keep **text in constants**, never hardcode
- ✅ Use **appropriate constant file** for each purpose

For more details, see:
- CONSTANTS_REFACTORING.md - Detailed migration guide
- BACKEND_REFACTORING_COMPLETE.md - Complete summary
- REFACTORING_FINAL_SUMMARY.md - Overall project summary
