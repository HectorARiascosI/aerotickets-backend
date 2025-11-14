package com.aerotickets.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio para envío de correos.
 * Actualmente usado para recuperación de contraseña.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@aerotickets.com}")
    private String fromAddress;

    /**
     * Envía un correo de recuperación de contraseña con el enlace proporcionado.
     * Se envía SIEMPRE al email que el usuario registró en el sistema.
     */
    public void sendPasswordResetEmail(String toEmail, String resetUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setFrom(fromAddress);
            message.setSubject("Recuperación de contraseña - Aerotickets");
            message.setText(buildResetBody(resetUrl));

            mailSender.send(message);
            log.info("📧 Email de recuperación enviado a {}", maskEmail(toEmail));
        } catch (Exception ex) {
            // Log interno, sin exponer datos sensibles
            log.error("❌ Error enviando email de recuperación: {}", ex.getMessage(), ex);
            // No lanzamos hacia afuera para no filtrar detalles de infraestructura
        }
    }

    private String buildResetBody(String resetUrl) {
        return """
                Hola,

                Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en Aerotickets.

                Para crear una nueva contraseña, haz clic en el siguiente enlace (o cópialo en tu navegador):

                %s

                Si tú no solicitaste este cambio, puedes ignorar este mensaje.

                Atentamente,
                Equipo Aerotickets
                """.formatted(resetUrl);
    }

    // Enmascara el email en logs para protección de datos (habeas data)
    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) return "***" + email.substring(atIndex);
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}