package com.aerotickets.exception;

import com.aerotickets.constants.GlobalExceptionConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        String body = GlobalExceptionConstants.MSG_BAD_REQUEST_PREFIX + ex.getMessage();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleConflict(IllegalStateException ex) {
        String body = GlobalExceptionConstants.MSG_CONFLICT_PREFIX + ex.getMessage();
        return ResponseEntity.status(409).body(body);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleUnauthorized(SecurityException ex) {
        String body = GlobalExceptionConstants.MSG_UNAUTHORIZED_PREFIX + ex.getMessage();
        return ResponseEntity.status(403).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception ex) {
        String body = GlobalExceptionConstants.MSG_INTERNAL_SERVER_ERROR_PREFIX + ex.getMessage();
        return ResponseEntity.internalServerError().body(body);
    }
}