package com.aerotickets.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final HttpHeaders JSON_HEADERS;
    static {
        JSON_HEADERS = new HttpHeaders();
        JSON_HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    // 401 - No autenticado
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException ex) {
        return new ResponseEntity<>(
                Map.of("message", "No autenticado", "type", ex.getClass().getSimpleName()),
                JSON_HEADERS, HttpStatus.UNAUTHORIZED
        );
    }

    // 403 - Sin permisos
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccess(AccessDeniedException ex) {
        return new ResponseEntity<>(
                Map.of("message", "Acceso denegado", "type", ex.getClass().getSimpleName()),
                JSON_HEADERS, HttpStatus.FORBIDDEN
        );
    }

    // 404 - Recurso no encontrado (genérico)
    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        return new ResponseEntity<>(
                Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Recurso no encontrado"),
                JSON_HEADERS, HttpStatus.NOT_FOUND
        );
    }

    // 400 - Petición inválida
    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex) {
        return new ResponseEntity<>(
                Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Petición inválida"),
                JSON_HEADERS, HttpStatus.BAD_REQUEST
        );
    }

    // 409 - Violación de integridad (BD)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex) {
        String rootMsg = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();
        return new ResponseEntity<>(
                Map.of("message", "Conflicto de datos. " + rootMsg),
                JSON_HEADERS, HttpStatus.CONFLICT
        );
    }

    // 422 - Errores de validación (DTOs con @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            errors.put(err.getField(), err.getDefaultMessage());
        }
        return new ResponseEntity<>(
                Map.of("message", "Error de validación de campos", "errors", errors),
                JSON_HEADERS, HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    // 422 - Violaciones de constraints sueltas
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException ex) {
        return new ResponseEntity<>(
                Map.of("message", "Violación de restricciones", "errors", ex.getMessage()),
                JSON_HEADERS, HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    // 500 - Cualquier otro error inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return new ResponseEntity<>(
                Map.of(
                        "message", "Error inesperado en el servidor",
                        "type", ex.getClass().getSimpleName()
                ),
                JSON_HEADERS, HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}