package com.aerotickets.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class EmailService {

    @Value("${app.mail.from:no-reply@aerotickets.com}")
    private String fromAddress;

    @Value("${sendgrid.api-key:}")
    private String sendgridApiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void sendPasswordResetEmail(String toEmail, String resetUrl) {
        if (sendgridApiKey == null || sendgridApiKey.isBlank()) {
            log.error("No se ha configurado SENDGRID_API_KEY. No se puede enviar el correo de recuperación.");
            return;
        }

        try {
            String url = "https://api.sendgrid.com/v3/mail/send";

            String textContent = String.format(
                    "Hola,%n%n" +
                    "Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en Aerotickets.%n%n" +
                    "Para crear una nueva contraseña, usa este enlace:%n%n%s%n%n" +
                    "Si tú no solicitaste este cambio, puedes ignorar este mensaje.%n%n" +
                    "Atentamente,%n" +
                    "Equipo Aerotickets",
                    resetUrl
            );

            String payload = """
                    {
                      "personalizations": [{
                        "to": [{ "email": "%s" }]
                      }],
                      "from": { "email": "%s", "name": "Aerotickets" },
                      "subject": "Recuperación de contraseña - Aerotickets",
                      "content": [{
                        "type": "text/plain",
                        "value": "%s"
                      }]
                    }
                    """.formatted(
                    escapeJson(toEmail),
                    escapeJson(fromAddress),
                    escapeJson(textContent)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + sendgridApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status >= 200 && status < 300) {
                log.info("Email de recuperación enviado a {}", maskEmail(toEmail));
            } else {
                log.error("Error en SendGrid (status {}): {}", status, response.body());
            }
        } catch (Exception ex) {
            log.error("Error enviando email de recuperación a {}: {}", maskEmail(toEmail), ex.getMessage(), ex);
        }
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) return "***" + email.substring(Math.max(atIndex, 0));
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}