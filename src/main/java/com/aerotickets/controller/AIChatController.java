package com.aerotickets.controller;

import com.aerotickets.dto.ChatMessageDTO;
import com.aerotickets.dto.ChatResponseDTO;
import com.aerotickets.service.AIChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/chat")
public class AIChatController {

    private final AIChatService aiChatService;

    public AIChatController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponseDTO> chat(
            @RequestBody ChatMessageDTO request,
            Authentication auth
    ) {
        try {
            String userEmail = (auth != null) ? auth.getName() : null;
            
            if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ChatResponseDTO("Por favor envía un mensaje válido"));
            }

            ChatResponseDTO response = aiChatService.processMessage(
                request.getMessage(),
                userEmail
            );

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error en AIChatController: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(500)
                .body(new ChatResponseDTO("Lo siento, ocurrió un error. Por favor intenta de nuevo."));
        }
    }
}
