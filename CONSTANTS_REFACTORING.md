# Backend Constants Refactoring

## Overview
This document describes the reorganization of constants in the Aerotickets backend to improve maintainability, consistency, and code quality.

## New Constants Structure

### Core Constants Files (Recommended to use)

1. **ApiPaths.java** - All API endpoint paths
   - Organized by controller (Auth, Users, Flights, Reservations, etc.)
   - Centralized route definitions

2. **ErrorMessages.java** - All error messages
   - Organized by domain (Auth, Flight, Reservation, Jwt)
   - User-facing error messages

3. **SuccessMessages.java** - All success messages
   - Organized by domain
   - User-facing success messages

4. **ValidationMessages.java** - Bean validation messages
   - Used with @NotNull, @NotBlank, etc.
   - Organized by entity/DTO type

5. **LogMessages.java** - Application logging messages
   - Organized by category (Startup, Email, Security)
   - Internal logging only

6. **ConfigKeys.java** - Configuration properties
   - Environment variables
   - Application.yml property keys
   - Default values

7. **HttpConstants.java** - HTTP-related constants
   - Headers, Methods, Content Types
   - CORS configuration

8. **SecurityPatterns.java** - Security patterns
   - Public endpoints
   - Protected endpoints
   - Ant patterns

9. **BusinessRules.java** - Business logic constants
   - Default values
   - Domain-specific rules
   - Calculation constants

## Migration Guide

### Old Files → New Files Mapping

| Old File | New File | Status |
|----------|----------|--------|
| AuthConstants.java | ApiPaths.Auth + ErrorMessages.Auth + ConfigKeys.Cors | Migrate |
| FlightConstants.java | ApiPaths.Flights + ErrorMessages.Flight + BusinessRules.Flight | Migrate |
| ReservationConstants.java | ApiPaths.Reservations + ErrorMessages.Reservation | Migrate |
| ReservationServiceConstants.java | ErrorMessages.Reservation | Migrate |
| UserConstants.java | ApiPaths.Users | Migrate |
| UserServiceConstants.java | ErrorMessages.Auth | Migrate |
| SecurityConstants.java | SecurityPatterns + HttpConstants | Migrate |
| SecurityMessagesConstants.java | ErrorMessages.Auth | Migrate |
| JwtConstants.java | ErrorMessages.Jwt + BusinessRules.Auth | Migrate |
| EmailConstants.java | LogMessages.Email + BusinessRules.Email + ConfigKeys.Email | Migrate |
| CacheConstants.java | ConfigKeys.Cache | Migrate |
| CorsConstants.java | ConfigKeys.Cors | Migrate |
| AppMessages.java | LogMessages.Startup + ConfigKeys.Environment | Migrate |

### Files to Keep (Domain-Specific)

These files contain domain-specific logic and should be kept:
- **FlightSeedConstants.java** - Flight seeding configuration
- **LiveFlightConstants.java** - Live flight API configuration
- **LiveFlightStatus.java** - Flight status enum/constants
- **DataLoaderConstants.java** - Data loading configuration
- **CatalogConstants.java** - Catalog-specific constants
- **FlywayConstants.java** - Database migration constants
- **HealthConstants.java** - Health check constants
- **TestConstants.java** - Test-specific constants

### Files to Review/Remove (Logging/Debug)

These files are for logging/debugging and may be consolidated:
- **HttpDumpConstants.java** - HTTP request/response dumping
- **PerformanceLoggingConstants.java** - Performance metrics
- **RequestLoggingConstants.java** - Request logging
- **StartupLogConstants.java** - Startup logging
- **GlobalExceptionConstants.java** - Exception handling
- **DtoValidationConstants.java** - DTO validation

## Benefits of New Structure

1. **Clear Separation of Concerns**
   - API paths separate from error messages
   - Configuration separate from business logic
   - Logging separate from user-facing messages

2. **Easier to Find Constants**
   - Logical grouping by purpose
   - Nested classes for sub-categories
   - Consistent naming conventions

3. **Better Maintainability**
   - Less duplication
   - Easier to update messages
   - Clear ownership of constants

4. **Internationalization Ready**
   - All user-facing text in one place
   - Easy to extract for translation
   - Consistent message format

5. **Type Safety**
   - Nested static classes prevent naming conflicts
   - Final classes prevent inheritance
   - Private constructors prevent instantiation

## Usage Examples

### Before (Old Structure)
```java
// Multiple files to import
import com.aerotickets.constants.AuthConstants;
import com.aerotickets.constants.ReservationConstants;
import com.aerotickets.constants.FlightConstants;

@RestController
@RequestMapping(AuthConstants.BASE_PATH)
public class AuthController {
    // ...
    throw new IllegalArgumentException(AuthConstants.MSG_EMAIL_ALREADY_REGISTERED);
}
```

### After (New Structure)
```java
// Clearer imports
import com.aerotickets.constants.ApiPaths;
import com.aerotickets.constants.ErrorMessages;

@RestController
@RequestMapping(ApiPaths.Auth.BASE)
public class AuthController {
    // ...
    throw new IllegalArgumentException(ErrorMessages.Auth.EMAIL_ALREADY_REGISTERED);
}
```

## Migration Steps

1. **Phase 1: Create New Files** ✅
   - All new constant files created
   - Organized by purpose

2. **Phase 2: Update Controllers**
   - Replace old imports with new ones
   - Update constant references
   - Test each controller

3. **Phase 3: Update Services**
   - Replace old imports with new ones
   - Update constant references
   - Test each service

4. **Phase 4: Update Configuration**
   - Update security config
   - Update CORS config
   - Update cache config

5. **Phase 5: Remove Old Files**
   - After all references updated
   - Remove deprecated constant files
   - Clean up imports

## Testing Checklist

- [ ] All controllers compile without errors
- [ ] All services compile without errors
- [ ] All tests pass
- [ ] API endpoints still work
- [ ] Error messages display correctly
- [ ] Security configuration works
- [ ] CORS configuration works
- [ ] Email sending works
- [ ] JWT authentication works
- [ ] Database migrations work

## Notes

- Keep old files until migration is complete
- Use @Deprecated annotation on old constants
- Update documentation as you migrate
- Test thoroughly after each phase
- Consider adding unit tests for constants usage

## Recommendations

1. **Use nested static classes** for logical grouping
2. **Make all constant classes final** to prevent inheritance
3. **Use private constructors** to prevent instantiation
4. **Group related constants** together
5. **Use descriptive names** that indicate purpose
6. **Add JavaDoc comments** for complex constants
7. **Keep constants immutable** (final fields)
8. **Avoid magic numbers** - use named constants
