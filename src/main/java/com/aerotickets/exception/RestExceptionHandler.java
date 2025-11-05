package com.aerotickets.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body("Solicitud inválida: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleConflict(IllegalStateException ex){
        return ResponseEntity.status(409).body("Conflicto: " + ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleUnauthorized(SecurityException ex){
        return ResponseEntity.status(403).body("No autorizado: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex){
        // log ex
        return ResponseEntity.internalServerError().body("Error interno del servidor: " + ex.getMessage());
    }
}
