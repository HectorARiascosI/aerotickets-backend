package com.aerotickets.constants;

public final class AuthConstants {

    private AuthConstants() {
    }

    public static final String BASE_PATH = "/auth";
    public static final String REGISTER_PATH = "/register";
    public static final String LOGIN_PATH = "/login";
    public static final String FORGOT_PASSWORD_PATH = "/forgot-password";
    public static final String RESET_PASSWORD_PATH = "/reset-password";

    public static final String CORS_ORIGIN_LOCAL = "http://localhost:5173";
    public static final String CORS_ORIGIN_VERCEL_MAIN = "https://aerotickets-frontend.vercel.app";
    public static final String CORS_ORIGIN_VERCEL_ENV =
            "https://aerotickets-frontend-iaqxjc453-hector-riascos-projects.vercel.app";

    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_TOKEN = "token";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_NEW_PASSWORD = "newPassword";
    public static final String FIELD_MESSAGE = "message";

    public static final String MSG_EMAIL_ALREADY_REGISTERED = "El correo ya está registrado.";
    public static final String MSG_USER_REGISTERED_SUCCESS = "Usuario registrado exitosamente";
    public static final String MSG_USER_NOT_FOUND = "Usuario no encontrado";

    public static final String MSG_EMAIL_REQUIRED = "El correo electrónico es obligatorio";
    public static final String MSG_TOKEN_REQUIRED = "El token de recuperación es obligatorio";
    public static final String MSG_NEW_PASSWORD_REQUIRED = "La nueva contraseña es obligatoria";
    public static final String MSG_PASSWORD_RESET_SENT =
            "Si el correo está registrado, te hemos enviado un enlace para restablecer tu contraseña.";
    public static final String MSG_INVALID_TOKEN_OR_USER = "Token inválido o usuario no encontrado";
    public static final String MSG_PASSWORD_UPDATED =
            "Tu contraseña ha sido actualizada correctamente. Ya puedes iniciar sesión.";

    public static final int TEMP_TOKEN_MINUTES = 10;
}