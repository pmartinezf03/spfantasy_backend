package com.spfantasy.backend.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spfantasy.backend.service.ChatBotService;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatBotController.class);

    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping("/{username}")
    public ResponseEntity<Map<String, String>> procesarPregunta(
            @PathVariable String username,
            @RequestBody Map<String, String> body) {

        logger.info("POST /api/chatbot/{} recibido", username);
        logger.info("Body recibido: {}", body);

        String pregunta = body.get("pregunta");
        logger.info("Pregunta extraída: {}", pregunta);

        try {
            String respuesta = chatBotService.procesarPregunta(username, pregunta);
            logger.info("Respuesta generada para {}: {}", username, respuesta);
            return ResponseEntity.ok(Map.of("respuesta", respuesta));
        } catch (Exception e) {
            logger.error("Error procesando la pregunta: ", e);
            return ResponseEntity.badRequest().body(Map.of("respuesta", "❌ Error: " + e.getMessage()));
        }
    }
}
