package com.aerotickets.constants;

public final class GlobalExceptionConstants {

    private GlobalExceptionConstants() {
    }

    public static final String MSG_UNAUTHENTICATED = "No autenticado";
    public static final String MSG_ACCESS_DENIED = "Acceso denegado";
    public static final String MSG_NOT_FOUND = "Recurso no encontrado";
    public static final String MSG_BAD_REQUEST = "Petición inválida";
    public static final String MSG_DATA_CONFLICT = "Conflicto de datos. ";
    public static final String MSG_VALIDATION_ERROR = "Error de validación de campos";
    public static final String MSG_CONSTRAINT_VIOLATION = "Violación de restricciones";
    public static final String MSG_INTERNAL_SERVER_ERROR = "Error inesperado en el servidor";

    public static final String MSG_BAD_REQUEST_PREFIX = "Solicitud inválida: ";
    public static final String MSG_CONFLICT_PREFIX = "Conflicto: ";
    public static final String MSG_UNAUTHORIZED_PREFIX = "No autorizado: ";
    public static final String MSG_INTERNAL_SERVER_ERROR_PREFIX = "Error interno del servidor: ";
}