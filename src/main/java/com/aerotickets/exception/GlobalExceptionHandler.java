package com.aerotickets.exception;

import com.aerotickets.constants.GlobalExceptionConstants;
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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException ex) {
        return new ResponseEntity<>(
                Map.of(
                        "message", GlobalExceptionConstants.MSG_UNAUTHENTICATED,
                        "type", ex.getClass().getSimpleName()
                ),
                JSON_HEADERS,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccess(AccessDeniedException ex) {
        return new ResponseEntity<>(
                Map.of(
                        "message", GlobalExceptionConstants.MSG_ACCESS_DENIED,
                        "type", ex.getClass().getSimpleName()
                ),
                JSON_HEADERS,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        String msg = ex.getMessage() != null
                ? ex.getMessage()
                : GlobalExceptionConstants.MSG_NOT_FOUND;

        return new ResponseEntity<>(
                Map.of("message", msg),
                JSON_HEADERS,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex) {
        String msg = ex.getMessage() != null
                ? ex.getMessage()
                : GlobalExceptionConstants.MSG_BAD_REQUEST;

        return new ResponseEntity<>(
                Map.of("message", msg),
                JSON_HEADERS,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex) {
        String rootMsg = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        return new ResponseEntity<>(
                Map.of("message", GlobalExceptionConstants.MSG_DATA_CONFLICT + rootMsg),
                JSON_HEADERS,
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            errors.put(err.getField(), err.getDefaultMessage());
        }

        return new ResponseEntity<>(
                Map.of(
                        "message", GlobalExceptionConstants.MSG_VALIDATION_ERROR,
                        "errors", errors
                ),
                JSON_HEADERS,
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException ex) {
        return new ResponseEntity<>(
                Map.of(
                        "message", GlobalExceptionConstants.MSG_CONSTRAINT_VIOLATION,
                        "errors", ex.getMessage()
                ),
                JSON_HEADERS,
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        ex.printStackTrace();

        return new ResponseEntity<>(
                Map.of(
                        "message", GlobalExceptionConstants.MSG_INTERNAL_SERVER_ERROR,
                        "type", ex.getClass().getSimpleName()
                ),
                JSON_HEADERS,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}