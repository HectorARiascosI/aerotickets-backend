package com.aerotickets.constants;

public final class EmailConstants {

    private EmailConstants() {
    }

    public static final String LOG_SENDGRID_MISSING_API_KEY =
            "No se ha configurado SENDGRID_API_KEY. No se puede enviar el correo de recuperación.";

    public static final String SUBJECT_PASSWORD_RESET = "Recuperación de contraseña - Aerotickets";
    public static final String SENDER_NAME = "Aerotickets";

    public static final String TEMPLATE_PASSWORD_RESET_TEXT =
            "Hola,%n%n" +
            "Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en Aerotickets.%n%n" +
            "Para crear una nueva contraseña, usa este enlace:%n%n%s%n%n" +
            "Si tú no solicitaste este cambio, puedes ignorar este mensaje.%n%n" +
            "Atentamente,%n" +
            "Equipo Aerotickets";

    public static final String LOG_PASSWORD_RESET_SENT = "Email de recuperación enviado a {}";
    public static final String LOG_SENDGRID_ERROR = "Error en SendGrid (status {}): {}";
    public static final String LOG_PASSWORD_RESET_ERROR =
            "Error enviando email de recuperación a {}: {}";
}