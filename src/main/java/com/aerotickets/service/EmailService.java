package com.aerotickets.service;

import com.aerotickets.constants.EmailConstants;
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
            log.error(EmailConstants.LOG_SENDGRID_MISSING_API_KEY);
            return;
        }

        try {
            String url = "https://api.sendgrid.com/v3/mail/send";

            String textContent = String.format(
                    EmailConstants.TEMPLATE_PASSWORD_RESET_TEXT,
                    resetUrl
            );

            String payload = """
                    {
                      "personalizations": [{
                        "to": [{ "email": "%s" }]
                      }],
                      "from": { "email": "%s", "name": "%s" },
                      "subject": "%s",
                      "content": [{
                        "type": "text/plain",
                        "value": "%s"
                      }]
                    }
                    """.formatted(
                    escapeJson(toEmail),
                    escapeJson(fromAddress),
                    escapeJson(EmailConstants.SENDER_NAME),
                    escapeJson(EmailConstants.SUBJECT_PASSWORD_RESET),
                    escapeJson(textContent)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + sendgridApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            int status = response.statusCode();

            if (status >= 200 && status < 300) {
                log.info(EmailConstants.LOG_PASSWORD_RESET_SENT, maskEmail(toEmail));
            } else {
                log.error(EmailConstants.LOG_SENDGRID_ERROR, status, response.body());
            }
        } catch (Exception ex) {
            log.error(
                    EmailConstants.LOG_PASSWORD_RESET_ERROR,
                    maskEmail(toEmail),
                    ex.getMessage(),
                    ex
            );
        }
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return "***" + email.substring(Math.max(atIndex, 0));
        }
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